/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.groovyloader.core

import org.codehaus.groovy.runtime.InvokerHelper
import org.apache.myfaces.groovyloader.core.ReloadingMetadata
import org.apache.myfaces.groovyloader.core.Groovy2GroovyObjectReloadingProxy
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.apache.myfaces.scripting.api.ScriptingWeaver

/**
 * Weaver  which does dynamic class reloading
 * and in groovy2groovy encironments also adds
 * a transparent reloading proxy
 * that way we get a dynamic reloading
 * upon file change
 * what we do now is to get a reload
 * interval working to improve performance
 *
 * this class is a singleton in the servlet context, and uses
 * a daemon thread to check for changed files for speed reasons
 *
 *
 * @author Werner Punz
 */
public class GroovyWeaver implements Serializable, ScriptingWeaver {

    static def gcl = null
    static Map classMap = Collections.synchronizedMap([:])

    static Log log = LogFactory.getLog(GroovyWeaver.class)

    public GroovyWeaver() {
    }

    /*
    * we set a deamon to enable webapp artefact change tainting
    * this should speed up operations because we do not have
    * to check on the filesystem for every reload access
    * (which happens a lot)
    * the daemon does not block the shutdown, however we should be notified
    * in case of a shutdown
    */
    static FileChangedDaemon changeWatcher = null;
    /*synchronized???*/


    private synchronized void startThread() {
        if (changeWatcher == null) {
            changeWatcher = new FileChangedDaemon(classMap)
        }
        changeWatcher.start()
    }

    private void stopThread() {
        if (changeWatcher != null) {
            changeWatcher.stop()
        }
    }

    public boolean isTainted(Class aClass, boolean newTaintedValue) {
        if (classMap.size() == 0) return false;

        def classReloadingMetaData = classMap[aClass.name]


        if (classReloadingMetaData == null) return false
        def oldTainted = classReloadingMetaData.tainted
        classReloadingMetaData.tainted = newTaintedValue
        return oldTainted
    }

    /**
     * recreates
     * and ignores our tainted code
     *
     * */
    public Object reloadScriptingInstance(Object o) {

        if (classMap.size() == 0)
            return o

        def reloadMeta = classMap[o.getClass().getName()];

        //This gives a minor speedup because we jump out as soon as possible
        //files never changed do not even have to be considered
        //not tained even once == not even considered to be reloaded
        if (isReloadCandidate(reloadMeta)) {

            //reload the class to get new static content if needed
            def aclass = reloadScriptingClass(o.class)
            if (aclass.hashCode().equals(o.class.hashCode())) {
                //class of this object has not changed although
                // reload is enabled we can skip the rest now
                return o
            }
            log.info("possible reload for ${o.class.name}")
            /*only recreation of empty constructor classes is possible*/
            try {
                //reload the object by instiating a new class and
                // assigning the attributes properly
                Object newObject = aclass.newInstance();

                /*now we shuffle the properties between the objects*/
                mapProperties(newObject, o)

                return newObject
            } catch (e) {
                log.error(e)
            }
        }
        return o;
    }

    /**
     * condition which marks a metadata as reload candidate
     */
    private boolean isReloadCandidate(reloadMeta) {
        return reloadMeta != null && reloadMeta.taintedOnce
    }


    /**
     * central algorithm which determines which property values are overwritten and which are not
     */
    private void mapProperties(def target, def src) {
        src.properties.each {property ->
            //ok here is the algorithm, basic datatypes usually are not copied but read in anew and then overwritten
            //later on
            //all others can be manually overwritten by adding an attribute <attributename>_changed

            try {
                if (target.properties.containsKey(property.key)
                    && !property.key.equals("metaClass")        //the class information and meta class information cannot be changed
                    && !property.key.equals("class")            //otherwise we will get following error
                                                                // java.lang.IllegalArgumentException: object is not an instance of declaring class
                    && !(
                    target.properties.containsKey(property.key + "_changed") //||
                    //nothing further needed the phases take care of that
                    )) {
                    target.setProperty(property.key, property.value)
                }
            } catch (Exception e) {

            }
        }
    }



    /**
     * reweaving of an existing woven class
     * by reloading its file contents and then reweaving it
     */
    public Class reloadScriptingClass(Class aclass) {

        ReloadingMetadata metadata = classMap[aclass.name]
        if (metadata == null)
            return aclass;
        if (!metadata.tainted) {
            //if not tained then we can recycle the last class loaded
            return metadata.aClass;
        }
        return loadScriptingClassFromFile(metadata.getFileName())
    }





    public Class loadScriptingClassFromFile(String file) {
        log.info("reloading $file")

        File currentClassFile = new File(file)

        //lazy instantiation for the gcl to eliminate a method
        if (gcl == null) {
            synchronized (this.class) {
                if (gcl != null) {
                    return;
                }
                gcl = new GroovyClassLoader(Thread.currentThread().contextClassLoader);
            }
        }

        def aclass = gcl.parseClass(new FileInputStream(currentClassFile))

        weaveGroovyReloadingCode(aclass)
        ReloadingMetadata reloadingMetaData = new ReloadingMetadata()


        reloadingMetaData.aClass = aclass;
        reloadingMetaData.fileName = file;
        reloadingMetaData.timestamp = currentClassFile.lastModified();
        reloadingMetaData.tainted = false;

        classMap.put(aclass.name, reloadingMetaData)
        /*we start our thread as late as possible due to groovy bugs*/

        startThread()
        return aclass

    }

    private final void weaveGroovyReloadingCode(Class aclass) {
        //TODO this only works in a groovy 2 groovy specific
        //surrounding lets check if this is not obsolete

        def myMetaClass = new Groovy2GroovyObjectReloadingProxy(aclass)
        def invoker = InvokerHelper.instance
        invoker.metaRegistry.setMetaClass(aclass, myMetaClass)
    }

}
