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

import org.apache.myfaces.groovyloader.core.Groovy2GroovyObjectReloadingProxy
import org.apache.myfaces.scripting.api.ScriptingConst
import org.apache.myfaces.scripting.api.ScriptingWeaver
import org.codehaus.groovy.runtime.InvokerHelper
import org.apache.myfaces.scripting.api.BaseWeaver
import org.apache.myfaces.scripting.loaders.groovy.DynamicClassIdentifier
import org.apache.myfaces.scripting.api.BaseWeaver

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
public class GroovyWeaver extends BaseWeaver implements Serializable, ScriptingWeaver {

    static ThreadLocal _groovyClassLoaderHolder = new ThreadLocal();
    DynamicClassIdentifier identifier = new DynamicClassIdentifier()


    public GroovyWeaver() {
        super();
        //super with params in java classes not superbly callable
        scriptingEngine = ScriptingConst.ENGINE_TYPE_GROOVY
        fileEnding = ".groovy"
    }



    /**
     * central algorithm which determines which property values are overwritten and which are not
     */
    protected void mapProperties(def target, def src) {
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



    protected Class loadScriptingClassFromFile(String sourceRoot, String file) {

        File currentClassFile = new File(sourceRoot + File.separator + file)

        if (file.contains("TestNavigationHandler")) {
            log.debug("debugpoint found");
        }

        if (!currentClassFile.exists()) {
            return null;
        }

        if (log.isInfoEnabled()) {
            log.info("Loading Groovy file:" + file);
        }
        //lazy instantiation to avoid threading problems
        //and locking related speed bumps

        //TODO replace the classloader detection with the one from the myfaces utils class
        GroovyClassLoader gcl = _groovyClassLoaderHolder.get()

        if (gcl == null) {
            gcl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
            //we have to add the script path so that groovy can work out the kinks of other source files added
            _groovyClassLoaderHolder.set(gcl)

            getScriptPaths().each {
                gcl.addClasspath(it)
            }
        }

        Class retVal = gcl.parseClass(new FileInputStream(currentClassFile))


        weaveGroovyReloadingCode(retVal)

        if (retVal != null) {
            refreshReloadingMetaData(sourceRoot, file, currentClassFile, retVal, ScriptingConst.ENGINE_TYPE_GROOVY);
        }


        return retVal

    }



    /**
     * creates a proxy specify object reloading proxy
     * this works very well in a groovy only specific
     * context, unfortunately as soon as the object
     * is pushed into java land the proxy is dropped
     * nevertheless we add this behavior for a groovy
     * only context, for now, in the long run we might
     * have to drop it entirely
     */
    private final void weaveGroovyReloadingCode(Class aclass) {
        //TODO this only works in a groovy 2 groovy specific
        //surrounding lets check if this is not obsolete

        def myMetaClass = new Groovy2GroovyObjectReloadingProxy(aclass)
        def invoker = InvokerHelper.instance
        invoker.metaRegistry.setMetaClass(aclass, myMetaClass)
    }

    public boolean isDynamic(Class clazz) {
        return identifier.isDynamic(clazz)
    }

}
