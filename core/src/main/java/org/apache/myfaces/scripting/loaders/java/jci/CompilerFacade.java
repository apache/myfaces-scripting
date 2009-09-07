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
package org.apache.myfaces.scripting.loaders.java.jci;

import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.EclipseJavaCompiler;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;
import org.apache.commons.jci.problems.CompilationProblem;

import java.io.File;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class CompilerFacade implements DynamicCompiler {
    JavaCompiler compiler = null;


    public CompilerFacade() {
        super();

        compiler = (new JavaCompilerFactory()).createCompiler("javac");

    }


    public Class compileFile(String sourceRoot, String classPath, String filePath) throws ClassNotFoundException {

        ResourceReader reader = new FileResourceReader(new File(sourceRoot));
        MemoryResourceStore target = new MemoryResourceStore();
        CompilationResult result = null;
        String[] toCompile = new String[]{filePath};
        String className = filePath.replaceAll(File.separator, ".");
        className = className.replaceAll("[\\\\\\/]", ".");
        className = className.substring(0, className.length() - 5);

        result = compiler.compile(toCompile, reader, target);

        if (result.getErrors().length == 0) {
            ResourceStore[] stores = {target};
            ResourceStoreClassLoader loader = new ResourceStoreClassLoader(Thread.currentThread().getContextClassLoader(), stores);
            return loader.loadClass(className);
        } else {
            Log log = LogFactory.getLog(this.getClass());
            for (CompilationProblem error : result.getErrors()) {
                log.error(error.getMessage());
            }
            for (CompilationProblem error : result.getWarnings()) {
                log.error(error.getMessage());
            }
        }
        return null;
    }
}
