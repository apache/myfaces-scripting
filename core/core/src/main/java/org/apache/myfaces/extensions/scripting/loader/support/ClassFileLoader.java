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
package org.apache.myfaces.extensions.scripting.loader.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>This class loader actually loads the newly compiled classes. Each newly compiled class
 * requires one to instantiate a new instance of this class loader, in doing so we don't cause
 * severe linkage errors (the JVM doesn't permit class loaders to load classes more than once).
 * That means on the other hand that each class loader is just going to load a single class.
 * </p>
 */
public class ClassFileLoader extends AbstractThrowAwayClassLoader {

    /**
     * The logger instance for this class.
     */
    private static final Log logger = LogFactory.getLog(ClassFileLoader.class);

    /**
     * The .class file that contains the bytecode for the class that this class loader is going to load.
     */
    private final File classFile;

    // -------------------------------------- Constructors

    /**
     * <p>Constructs a new class loader that is just going to load the given class file. If one
     * requests to load a different class than that, this class loader will just delegate the
     * request to the given parent class loader.</p>
     *
     * @param className         the name of the class that this class loader is going to load
     * @param classFile         a reference to the .class file that contains the bytecode for the class
     * @param parentClassLoader the parent class loader
     */
    public ClassFileLoader(String className, File classFile, ClassLoader parentClassLoader) {
        super(className, parentClassLoader);

        if (classFile == null) {
            throw new IllegalArgumentException("The given class file must not be null.");
        }

        this.classFile = classFile;
    }

    // ------------------------------------------ URLClassLoader methods

    /**
     * <p>Returns the search path of URLs for loading classes, i.e. the
     * given compilation target directory, the directory that contains the
     * dynamically compiled .class files.</p>
     *
     * @return the search path of URLs for loading classes
     */
    public URL[] getURLs() {
        try {
            return new URL[]{classFile.toURI().toURL()};
        } catch (IOException ex) {
            logger.error("Couldn't resolve the URL to the class file '"
                    + classFile + "' that this class loader '" + this + "' should load.", ex);
            return new URL[0];
        }
    }

    // -------------------------------------- ThrowAwayClassLoader methods

    /**
     * <p>Opens a stream to the resource that defines the given class. If it
     * cannot be found, return <code>null</code>.</p>
     *
     * @param className the class to load
     * @return a stream to the resource that defines the given class
     * @throws java.io.IOException if an I/O error occurs
     */
    protected InputStream openStreamForClass(String className) throws IOException {
        return new FileInputStream(classFile);
    }

}
