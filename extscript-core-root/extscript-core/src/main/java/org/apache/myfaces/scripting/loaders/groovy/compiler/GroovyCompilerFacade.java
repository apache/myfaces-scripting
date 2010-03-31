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
package org.apache.myfaces.scripting.loaders.groovy.compiler;

import org.apache.myfaces.scripting.api.CompilationResult;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.FileUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.groovy.GroovyRecompiledClassloader;
import org.apache.myfaces.scripting.sandbox.compiler.GroovyCompiler;

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

public class GroovyCompilerFacade implements DynamicCompiler {

    //ContainerFileManager fileManager = null;

    Logger _log = Logger.getLogger(this.getClass().getName());
    GroovyCompiler compiler;

    public GroovyCompilerFacade() {
        super();

        compiler = new GroovyCompiler();
        //fileManager = new GroovyContainerFileManager();
    }

    public Class compileFile(String sourceRoot, String classPath, String filePath) throws ClassNotFoundException {

        String separator = FileUtils.getFileSeparatorForRegex();
        String className = filePath.replaceAll(separator, ".");
        className = ClassUtils.relativeFileToClassName(className);

        // try {
        //no need we do a full recompile at the beginning
        //CompilationResult result = compiler.compile(new File(sourceRoot), fileManager.getTempDir(), filePath, fileManager.getClassLoader());

        //displayMessages(result);

        //if (!result.hasErrors()) {
        GroovyRecompiledClassloader classLoader = new GroovyRecompiledClassloader(ClassUtils.getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_GROOVY, ".groovy");

        //fileManager.refreshClassloader();
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        //we now quickly check for the groovy classloader being, set we cannot deal with instances here

        //if (!(oldClassLoader.equals(fileManager.getClassLoader()))) {
        try {
            //RecompiledClassLoader classLoader = (RecompiledClassLoader) fileManager.getClassLoader();
            classLoader.setSourceRoot(sourceRoot);
            Thread.currentThread().setContextClassLoader(classLoader);

            //Not needed anymore due to change in the dynamic class detection system
            //ClassUtils.markAsDynamicJava(fileManager.getTempDir().getAbsolutePath(), className);
            if(className.startsWith("groovy.")) {
                _log.finer("debugpoint found");
            }

            return classLoader.loadClass(className);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        //}
        //} else {
        //    log.error("Compiler errors");
        //    displayMessages(result);
        //}

        //} catch (CompilationException e) {
        //    log.error(e);
        //}
        //return null;
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
        GroovyRecompiledClassloader classLoader = new GroovyRecompiledClassloader(ClassUtils.getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_GROOVY, ".groovy");
        classLoader.setSourceRoot(sourceRoot);
        CompilationResult result = compiler.compile(new File(sourceRoot), WeavingContext.getConfiguration().getCompileTarget(), classLoader);

        displayMessages(result);
        return WeavingContext.getConfiguration().getCompileTarget();
    }

    private void displayMessages(CompilationResult result) {
        for (CompilationResult.CompilationMessage error : result.getErrors()) {
            _log.log(Level.WARNING, "[EXT-SCRIPTING] Groovy compiler error: {0} - {1}", new String[]{Long.toString(error.getLineNumber()), error.getMessage()});
        }
        for (CompilationResult.CompilationMessage error : result.getWarnings()) {
            _log.log(Level.WARNING, "[EXT-SCRIPTING] Groovy compiler warning: {0}", error.getMessage());
        }
        WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_GROOVY, result);
    }
}
