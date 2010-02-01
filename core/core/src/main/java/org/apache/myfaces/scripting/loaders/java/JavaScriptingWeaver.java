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

    DynamicClassIdentifier identifier = new DynamicClassIdentifier();

    /**
     * helper to allow initial compiler classpath scanning
     *
     * @param servletContext
     */
    public JavaScriptingWeaver(ServletContext servletContext) {
        super(ScriptingConst.JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JAVA);
        init();

    }

    private void init() {
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

   

    public JavaScriptingWeaver() {
        super(ScriptingConst.JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JAVA);
    }


   

    protected String getLoadingInfo(String file) {
        return "[EXT-SCRIPTING] Loading Java file:" + file;
    }

    private String getScriptingFacadeClass() {
        String javaVer = System.getProperty("java.version");
        String[] versionArr = javaVer.split("\\.");

        int major = Integer.parseInt(versionArr[Math.min(versionArr.length, 1)]);

        if (major > 5) {
            //jsr199 compliant jdk
            return ScriptingConst.JSR199_COMPILER;
        }
        //otherwise
        return ScriptingConst.JAVA5_COMPILER;
    }

    public boolean isDynamic(Class clazz) {
        return identifier.isDynamic(clazz);  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * checks outside of the request scope for changes and taints the corresponding engine
     */
    public void scanForAddedClasses() {
        _dependencyScanner.scanAndMarkChange();
    }
   

    protected DynamicCompiler instantiateCompiler() {
        return (DynamicCompiler) ReflectUtil.instantiate(getScriptingFacadeClass());
    }

}
