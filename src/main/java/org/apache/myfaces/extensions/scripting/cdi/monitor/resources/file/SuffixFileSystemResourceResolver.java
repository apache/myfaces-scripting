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
package org.apache.myfaces.extensions.scripting.cdi.monitor.resources.file;

import java.io.File;

/**
 *
 */
public class SuffixFileSystemResourceResolver extends FileSystemResourceResolver {

    /**
     * The file suffix that a file must have such that
     * this resource resolver takes it into consideration.
     */
    private String fileSuffix;

    // ------------------------------------------ Constructors

    /**
     * <p>Constructs a new suffix-based FileSystemResourceResolver using the given root directory
     * and the given file suffix. Note that this suffix determines whether this resource resolver
     * will take any file into consideration or not, it depends on whether it ends with the given
     * suffix. Even though you can use any suffix you want, you'll probably only use it for file
     * types, like for example, ".java", ".class", or ".groovy".</p>
     *
     * @param rootDirectory the root directory, i.e. the directory where to start looking for files
     * @param fileSuffix the file suffix that a file must have
     */
    public SuffixFileSystemResourceResolver(File rootDirectory, String fileSuffix) {
        super(rootDirectory);

        if (fileSuffix == null || fileSuffix.isEmpty()) {
            throw new IllegalArgumentException(
                    "The given file suffix must not be null.");
        }

        this.fileSuffix = fileSuffix;
    }

    // ------------------------------------------ FileSystemResourceResolver methods

    /**
     * <p>Checks whether the given file ends with the file suffix that
     * has been supplied during construction.</p>
     *
     * @param file the file that the resource resolver wants to check
     * 
     * @return <code>true</code> if the given file ends with the desired
     *          suffix, <code>false</code> otherwise
     */
    @Override
    protected boolean matches(File file) {
        return file.getName().endsWith(fileSuffix);
    }
    
}