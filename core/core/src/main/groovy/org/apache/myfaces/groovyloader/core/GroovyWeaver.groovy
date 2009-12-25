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

import org.apache.myfaces.scripting.api.ScriptingConst
import org.apache.myfaces.scripting.api.ScriptingWeaver
import org.codehaus.groovy.runtime.InvokerHelper
import org.apache.myfaces.scripting.api.BaseWeaver
import org.apache.myfaces.scripting.loaders.groovy.DynamicClassIdentifier

import org.apache.myfaces.scripting.core.util.WeavingContext

import org.apache.myfaces.scripting.api.ClassScanner

import org.apache.myfaces.scripting.core.util.ClassUtils

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
    ClassScanner _scanner = null;



    public GroovyWeaver() {
        super();
        //super with params in java classes not superbly callable
        //FIXME this is private in super class
        scriptingEngine = ScriptingConst.ENGINE_TYPE_GROOVY
        fileEnding = ".groovy"

        //the super pass down between groovy and java is broken for the current
        //version we work around that with setters
        _reloadingStrategy = new GroovyGlobalReloadingStrategy()
        _reloadingStrategy.setWeaver(this)

        //init classpath removed we can resolve that over the
        //url classloader at the time myfaces is initialized
        try {
            Class scanner = ClassUtils.getContextClassLoader().loadClass("org.apache.myfaces.scripting.jsf2.annotation.GenericAnnotationScanner");
            Class[] params = new Class[1];
            params[0] = ScriptingWeaver.class;
            this._scanner = scanner.getConstructor(params).newInstance(this);
            //this._scanner = (ClassScanner) ReflectUtil.instantiate(scanner, params);

        } catch (ClassNotFoundException e) {
            //we do nothing here
        }

    }

    /**
     * central point for the
     * loading, loads a class from a given sourceroot
     * and file
     */
    protected Class loadScriptingClassFromFile(String sourceRoot, String file) {

        File currentClassFile = new File(sourceRoot + File.separator + file)

        if (!currentClassFile.exists()) {
            return null;
        }

        if (log.isInfoEnabled()) {
            log.info("[EXT-SCRIPTING] Loading Groovy file:" + file);
        }
        //lazy instantiation to avoid threading problems
        //and locking related speed bumps

        //TODO replace the classloader detection with the one from the myfaces utils class
        GroovyClassLoader gcl = _groovyClassLoaderHolder.get()

        if (gcl == null) {
            gcl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
            //we have to add the script path so that groovy can work out the kinks of other source files added
            _groovyClassLoaderHolder.set(gcl)

            WeavingContext.getConfiguration().getSourceDirs(ScriptingConst.ENGINE_TYPE_GROOVY).each {
                gcl.addClasspath(it)
            }
        }

        Class retVal = gcl.parseClass(new FileInputStream(currentClassFile))


        weaveGroovyReloadingCode(retVal)

        if (retVal != null) {
            refreshReloadingMetaData(sourceRoot, file, currentClassFile, retVal, ScriptingConst.ENGINE_TYPE_GROOVY);
        }

        if (_scanner != null && retVal != null) {
            _scanner.scanClass(retVal);
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

    public void fullRecompile() {
        // We do not have to do a full recompile here
        //the groovy classloader takes care of the issue
        //instead we just set the recompile recommended to false

        WeavingContext.getRefreshContext().setRecompileRecommended(ScriptingConst.ENGINE_TYPE_GROOVY, Boolean.FALSE);
    }


    public void fullClassScan() {
        if (_scanner == null) {
            return;
        }
        _scanner.scanPaths();
    }

}
