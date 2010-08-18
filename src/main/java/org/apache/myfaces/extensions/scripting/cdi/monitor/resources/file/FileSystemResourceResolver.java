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

import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.ResourceResolver;

import java.io.File;

/**
 * 
 */
public class FileSystemResourceResolver implements ResourceResolver {

    private File rootDirectory;

    // ------------------------------------------ Constructors

    public FileSystemResourceResolver(File rootDirectory) {
        if (rootDirectory == null) {
            throw new IllegalArgumentException(
                    "The given root directory must not be null.");
        }

        this.rootDirectory = rootDirectory;
    }

    // ------------------------------------------ ResourceResolver methods

    public void resolveResources(ResourceCallback resourceHandler) {
        resolveResources(resourceHandler, rootDirectory);
    }

    private boolean resolveResources(ResourceCallback resourceHandler, File currentDirectory) {
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    boolean shallContinue = resolveResources(resourceHandler, file);
                    if (!shallContinue) {
                        return false;
                    }
                } else {
                    if (matches(file)) {
                        boolean shallContinue = resourceHandler.handle(new FileSystemResource(file));
                        if (!shallContinue) {
                            return true;
                        }
                    }
                }
            }
        }

        return true;
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Template method that enables subclasses to filter files, which means depending on the
     * boolean value that this method returns, the given file will be processed or not (which
     * again means, it will be forwarded to the resource handler).</p>
     * 
     * @param file the file you may want to filter
     * 
     * @return <code>true</code> if this file should be processed, <code>false</code> otherwise
     */
    protected boolean matches(File file) {
        return true;
    }
    
}