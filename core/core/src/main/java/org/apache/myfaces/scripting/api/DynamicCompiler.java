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
package org.apache.myfaces.scripting.api;

import java.io.File;

/**
 * @author Werner Punz
 *         Interface marking generic compiler facades which can
 *         plug various compiler backends into our system
 *         (for now jsr 199 is supported but in the long run JCI will
 *         be integrated for pre 1.6 jdks)
 *         <p/>
 *         Note the class does not have to be thread safe, the
 *         callers have to take care of the synchronisation
 *         the class is definitely called synchronized to avoid
 *         the windows file locking issues
 */
public interface DynamicCompiler {
    /**
     * compiles a single file into a class
     *
     * @param sourceRoot the source search path (root of our source)
     * @param classPath the classpath for the compiler
     * @param filePath   the relative path of our file
     * @return a valid java class of our file
     * @throws ClassNotFoundException in case of the class neither could be found
     *                                in our sources nor could be referenced in binary form from the classloader
     */
    public Class compileFile(String sourceRoot, String classPath, String filePath) throws ClassNotFoundException;

    public File compileAllFiles(String sourceRoot, String classPath) throws ClassNotFoundException;
}
