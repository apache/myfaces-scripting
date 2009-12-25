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
package org.apache.myfaces.scripting.loaders.java;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.api.*;
import org.apache.myfaces.scripting.core.util.*;
//import org.apache.myfaces.scripting.loaders.java.jsr199.ReflectCompilerFacade;

import javax.servlet.ServletContext;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * @author werpu
 *         <p/>
 *         The Scripting Weaver for the java core which reloads the java scripts
 *         dynamically upon change
 *         <p/>
 *         <p/>
 *         Note this is the central focus point for all reloading related activity
 *         this class introduces the correct class loader
 *         it manages the bean reloading on the proper stage of the lifecyle,
 *         calls the compilers single and all compile,
 *         it adds the strategies for the property handling of the reloaded instance
 *         <p/>
 *         Every language implementation has to implement this weaver
 *         and (if not done differently) also the proper compiler bindings
 *         and property handling strategies.
 */
public class JavaScriptingWeaver extends BaseWeaver implements ScriptingWeaver, Serializable {

    Log log = LogFactory.getLog(JavaScriptingWeaver.class);
    String classPath = "";
    DynamicClassIdentifier identifier = new DynamicClassIdentifier();

    private static final String JAVA_FILE_ENDING = ".java";
    private static final String JSR199_COMPILER = "org.apache.myfaces.scripting.loaders.java.jsr199.JSR199Compiler";
    private static final String JAVA5_COMPILER = "org.apache.myfaces.scripting.loaders.java.jdk5.CompilerFacade";

    ClassScanner _annotationScanner = null;
    ClassScanner _dependencyScanner = null;

    DynamicCompiler compiler = null;

    /**
     * helper to allow initial compiler classpath scanning
     *
     * @param servletContext
     */
    public JavaScriptingWeaver(ServletContext servletContext) {
        super(JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JAVA);
        //init classpath removed we can resolve that over the
        //url classloader at the time myfaces is initialized
        try {
            Class scanner = ClassUtils.getContextClassLoader().loadClass("org.apache.myfaces.scripting.jsf2.annotation.GenericAnnotationScanner");
            this._annotationScanner = (ClassScanner) ReflectUtil.instantiate(scanner, new Cast(ScriptingWeaver.class, this));
            
        } catch (ClassNotFoundException e) {
            //we do nothing here
        }

        this._dependencyScanner = new JavaDependencyScanner(this);
        

    }

    @Override
    public void appendCustomScriptPath(String scriptPath) {
        super.appendCustomScriptPath(scriptPath);
        if (_annotationScanner != null) {
            _annotationScanner.addScanPath(scriptPath);
        }
        _dependencyScanner.addScanPath(scriptPath);
    }

    public JavaScriptingWeaver() {
        super(JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JAVA);
    }


    /**
     * loads a class from a given sourceroot and filename
     * note this method does not have to be thread safe
     * it is called in a thread safe manner by the base class
     *
     * //TODO eliminate the source root we have the roots now somewhere else
     *
     * @param sourceRoot the source search lookup path
     * @param file       the filename to be compiled and loaded
     * @return a valid class if it could be found, null if none was found
     */
    @Override
    protected Class loadScriptingClassFromFile(String sourceRoot, String file) {
        //we load the scripting class from the given className

        File currentClassFile = new File(sourceRoot + File.separator + file);
        if (!currentClassFile.exists()) {
            return null;
        }

        if (log.isInfoEnabled()) {
            log.info("[EXT-SCRIPTING] Loading Java file:" + file);
        }

        Iterator<String> it = WeavingContext.getConfiguration().getSourceDirs(getScriptingEngine()).iterator();
        Class retVal = null;

        try {
            //we initialize the compiler lazy
            //because the facade itself is lazy
            if (compiler == null) {
                compiler = (DynamicCompiler) ReflectUtil.instantiate(getScriptingFacadeClass());//new ReflectCompilerFacade();
            }
            retVal = compiler.compileFile(sourceRoot, classPath, file);

            if (retVal == null) {
                return retVal;
            }
        } catch (ClassNotFoundException e) {
            //can be safely ignored
        }


      //no refresh needed because this is done in the case of java already by
      //the classloader  
      //  if (retVal != null) {
       //     refreshReloadingMetaData(sourceRoot, file, currentClassFile, retVal, ScriptingConst.ENGINE_TYPE_JAVA);
      //  }

        /**
         * we now scan the return value and update its configuration parameters if needed
         * this can help to deal with method level changes of class files like managed properties
         * or scope changes from shorter running scopes to longer running ones
         * if the annotation has been moved the class will be deregistered but still delivered for now
         *
         * at the next refresh the second step of the registration cycle should pick the new class up
         * //TODO we have to mark the artefacting class as deregistered and then enforce
         * //a reload this is however not the scope of the commit of this subtask
         * //we only deal with class level reloading here
         * //the deregistration notification should happen on artefact level (which will be the next subtask)
         */
        if (_annotationScanner != null && retVal != null) {
            _annotationScanner.scanClass(retVal);
        }

        return retVal;
    }

    private String getScriptingFacadeClass() {
        String javaVer = System.getProperty("java.version");
        String[] versionArr = javaVer.split("\\.");

        int major = Integer.parseInt(versionArr[Math.min(versionArr.length, 1)]);

        if (major > 5) {
            //jsr199 compliant jdk
            return JSR199_COMPILER;
        }
        //otherwise 
        return JAVA5_COMPILER;
    }

    public boolean isDynamic(Class clazz) {
        return identifier.isDynamic(clazz);  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * full scan, scans for all artefacts in all files
     */
    public void fullClassScan() {
        _dependencyScanner.scanPaths();
        
        if (_annotationScanner == null) {
            return;
        }

        _annotationScanner.scanPaths();

    }

    public void fullRecompile() {
        if (isFullyRecompiled()) {
            return;
        }

        if (compiler == null) {
            compiler = (DynamicCompiler) ReflectUtil.instantiate(getScriptingFacadeClass());//new ReflectCompilerFacade();
        }

        for (String scriptPath : WeavingContext.getConfiguration().getSourceDirs(getScriptingEngine())) {
            //compile via javac dynamically, also after this block dynamic compilation
            //for the entire length of the request,
            try {
                compiler.compileAllFiles(scriptPath, classPath);
            } catch (ClassNotFoundException e) {
                log.error(e);
            }

        }

        markAsFullyRecompiled();
    }


    private void markAsFullyRecompiled() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            //mark the request as tainted with recompile
            if (context != null) {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                requestMap.put(JavaScriptingWeaver.class.getName() + "_recompiled", Boolean.TRUE);
            }
        }
        WeavingContext.getRefreshContext().setRecompileRecommended(ScriptingConst.ENGINE_TYPE_JAVA, Boolean.FALSE);
    }

    private boolean isFullyRecompiled() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            return context.getExternalContext().getRequestMap().containsKey(JavaScriptingWeaver.class.getName() + "_recompiled");
        }
        return false;
    }

}
