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
package org.apache.myfaces.extensions.scripting.loaders.java;

import org.apache.myfaces.extensions.scripting.api.*;
import org.apache.myfaces.extensions.scripting.core.util.*;
import org.apache.myfaces.extensions.scripting.loaders.java.compiler.CompilerFacade;
//import org.apache.myfaces.extensions.scripting.loaders.java.jsr199.ReflectCompilerFacade;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class JavaScriptingWeaver extends BaseWeaver implements Serializable {

    private static final long serialVersionUID = -3024995032644947216L;

    transient DynamicClassIdentifier _identifier = new DynamicClassIdentifier();

    transient Logger _logger = Logger.getLogger(JavaScriptingWeaver.class.getName());

    /**
     * helper to allow initial compiler classpath scanning
     *
     * @param servletContext the servlet context
     */
    @SuppressWarnings("unused")
    public JavaScriptingWeaver(ServletContext servletContext) {
        super(ScriptingConst.JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JSF_JAVA);
        init();

    }

    private void init() {
        //init classpath removed we can resolve that over the
        //url classloader at the time myfaces is initialized
        try {
            Class scanner = ClassUtils.getContextClassLoader().loadClass("org.apache.myfaces.extensions.scripting.jsf2.annotation.GenericAnnotationScanner");
            this._annotationScanner = (ClassScanner) ReflectUtil.instantiate(scanner, new Cast(ScriptingWeaver.class, this));

        } catch (ClassNotFoundException e) {
            //we do nothing here
            //generic annotation scanner can be missing in jsf 1.2 environments
            _logger.log(Level.FINER, "", e);
        }

        this._dependencyScanner = new JavaDependencyScanner(this);
    }

    public JavaScriptingWeaver() {
        super(ScriptingConst.JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JSF_JAVA);
    }

    protected String getLoadingInfo(String file) {
        return "[EXT-SCRIPTING] Loading Java file:" + file;
    }

    public boolean isDynamic(Class clazz) {
        return _identifier.isDynamic(clazz);
    }

    /**
     * checks outside of the request scope for changes and taints the corresponding engine
     */
    public void scanForAddedClasses() {
        _dependencyScanner.scanAndMarkChange();
    }

    protected DynamicCompiler instantiateCompiler() {
        return new CompilerFacade();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _identifier = new DynamicClassIdentifier();
        Logger.getLogger(JavaScriptingWeaver.class.getName());
    }

}
