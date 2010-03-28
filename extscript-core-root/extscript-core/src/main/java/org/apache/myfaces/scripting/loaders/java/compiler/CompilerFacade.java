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
package org.apache.myfaces.scripting.loaders.java.compiler;

import org.apache.myfaces.scripting.api.CompilationException;
import org.apache.myfaces.scripting.api.CompilationResult;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.FileUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.java.RecompiledClassLoader;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Custom compiler call for jdk5
 *          we can call javac directly
 */

public class CompilerFacade implements DynamicCompiler {
    protected org.apache.myfaces.scripting.api.Compiler _compiler = null;

    Logger _log = Logger.getLogger(this.getClass().getName());

    public CompilerFacade() {
        super();

        _compiler = JavaCompilerFactory.getInstance().getCompilerInstance();
    }

    public CompilerFacade(boolean allowJSR) {
        super();

        _compiler = JavaCompilerFactory.getInstance().getCompilerInstance(allowJSR);
    }

    /**
     * does a compilation of all files one compile per request
     * is allowed for performance reasons, the request blocking will be done
     * probably on the caller side of things
     *
     * @param sourceRoot
     * @param classPath
     */
    public void compileAll(String sourceRoot, String classPath) {
        try {
            //TODO do a full compile and block the compile for the rest of the request
            //so that we do not run into endless compile cycles
            RecompiledClassLoader classLoader = new RecompiledClassLoader(ClassUtils.getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, ".java");
            classLoader.setSourceRoot(sourceRoot);
            CompilationResult result = _compiler.compile(new File(sourceRoot), WeavingContext.getConfiguration().getCompileTarget(), classLoader);
            displayMessages(result);
            if (result.hasErrors()) {
                _log.log(Level.WARNING, "Compiler output:{0}", result.getCompilerOutput());
            }

        } catch (org.apache.myfaces.scripting.api.CompilationException e) {
            _log.log(Level.SEVERE, "CompilationException : ", e);
        }
    }

    public Class compileFile(String sourceRoot, String classPath, String filePath) throws ClassNotFoundException {

        String separator = FileUtils.getFileSeparatorForRegex();
        String className = filePath.replaceAll(separator, ".");
        className = ClassUtils.relativeFileToClassName(className);
        RecompiledClassLoader classLoader = new RecompiledClassLoader(ClassUtils.getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, ".java");
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            classLoader.setSourceRoot(sourceRoot);
            Thread.currentThread().setContextClassLoader(classLoader);

            return classLoader.loadClass(className);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

    }

    /**
     * compiles all files
     *
     * @param sourceRoot the source root
     * @param classPath  the class path
     * @return the root target path for the classes which are compiled
     *         so that they later can be picked up by the classloader
     * @throws ClassNotFoundException
     */
    public File compileAllFiles(String sourceRoot, String classPath) throws ClassNotFoundException {
        try {
            RecompiledClassLoader classLoader = new RecompiledClassLoader(ClassUtils.getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, ".java");

            CompilationResult result = _compiler.compile(new File(sourceRoot), WeavingContext.getConfiguration().getCompileTarget(), classLoader);

            classLoader.setSourceRoot(sourceRoot);
            displayMessages(result);
            return WeavingContext.getConfiguration().getCompileTarget();
        } catch (CompilationException e) {
            _log.log(Level.SEVERE, "CompilationException :", e);
        }
        return null;
    }

    private void displayMessages(CompilationResult result) {
        for (CompilationResult.CompilationMessage error : result.getErrors()) {
            _log.log(Level.WARNING, "[EXT-SCRIPTING] Compile Error: {0} - {1}", new String[]{Long.toString(error.getLineNumber()), error.getMessage()});

        }
        for (CompilationResult.CompilationMessage error : result.getWarnings()) {
            _log.warning(error.getMessage());
        }
    }

}
