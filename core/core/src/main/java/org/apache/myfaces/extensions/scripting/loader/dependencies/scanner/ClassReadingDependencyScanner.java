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
package org.apache.myfaces.extensions.scripting.loader.dependencies.scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.extensions.scripting.loader.dependencies.registry.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.loader.dependencies.scanner.adapter.ClassVisitorAdapter;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 */
public class ClassReadingDependencyScanner implements DependencyScanner {

    /** The logger instance for this class. */
    private static final Log logger = LogFactory.getLog(ClassReadingDependencyScanner.class);

    // ------------------------------------------ DependencyScanner methods

    public void scan(DependencyRegistry registry, ClassLoader classLoader, String className) {
        try {
            InputStream classStream =
                    classLoader.getResourceAsStream(className.replace('.', '/') + ".class");
            ClassReader reader = new ClassReader(classStream);
            reader.accept(new ClassScanner(registry), 0);
        } catch (IOException ex) {
            logger.error("An I/O error occurred while scanning the dependencies of the class '"
                    + className + "' using the class loader '" + classLoader + "'.", ex);
        }
    }

    // ------------------------------------------ Private classes

    private class ClassScanner extends ClassVisitorAdapter {

        private DependencyRegistry registry;

        // -------------------------------------- Constructors

        public ClassScanner(DependencyRegistry registry) {
            this.registry = registry;
        }

        // -------------------------------------- ClassVisitor methods

        

    }
    
}
