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
package org.apache.myfaces.extensions.scripting.compiler;

import java.io.File;

/**
 * <p>An abstract compiler interface that enables you to compile one particular file at a time.</p>
 *
 */
public interface Compiler {

    /**
     * <p>Compiles the given file and creates an according class file in the given target path. Note that
     * it is possible for the given class to reference any other classes as long as the dependent classes
     * are available on the classpath. The given class loader determines the classes that are available
     * on the classpath.</p>
     *
     * @param sourcePath  the path to the source directory
     * @param targetPath  the path to the target directory
     * @param file        the file of the class you want to compile
     * @param classLoader the class loader for dependent classes
     * 
     * @return the compilation result, i.e. the compiler output, a list of errors and a list of warnings
     *
     * @throws CompilationException if a severe error occured while trying to compile a file
     */
    public CompilationResult compile(File sourcePath, File targetPath, File file, ClassLoader classLoader)
            throws CompilationException;

    /**
     * <p>Compiles the given file and creates an according class file in the given target path. Note that
     * it is possible for the given class to reference any other classes as long as the dependent classes
     * are available on the classpath. The given class loader determines the classes that are available
     * on the classpath.</p>
     *
     * @param sourcePath  the path to the source directory
     * @param targetPath  the path to the target directory
     * @param file        the relative file name of the class you want to compile
     * @param classLoader the class loader for dependent classes
     * 
     * @return the compilation result, i.e. the compiler output, a list of errors and a list of warnings
     *
     * @throws CompilationException if a severe error occured while trying to compile a file
     */
    public CompilationResult compile(File sourcePath, File targetPath, String file, ClassLoader classLoader)
            throws CompilationException;

}