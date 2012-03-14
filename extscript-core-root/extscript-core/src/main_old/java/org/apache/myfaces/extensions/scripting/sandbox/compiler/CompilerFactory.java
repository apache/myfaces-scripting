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
package org.apache.myfaces.extensions.scripting.sandbox.compiler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This factory chooses and creates an instance of the according compiler
 * implementation based on the current JVM for you. If you're using a Java 6
 * VM, it will return a compiler using the JSR-199 API, otherwise it will
 * return a compiler that uses the JavaC tool.</p>
 */
public class CompilerFactory {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(CompilerFactory.class.getName());

    // ------------------------------------------ Public methods

    /**
     * <p>Factory method that creates a new Java compiler depending on the
     * Java runtime that this application is running on. That means, if the
     * Java runtime supports the JSR-199 API (i.e. it's at least a Java 6
     * runtime) this API will be used. Otherwise a compiler will be returned
     * that tries to use some internal JDK classes.</p>
     *
     * @return a new Java compiler depending on the Java runtime
     */
    public static Compiler createCompiler() {
        if (logger.isLoggable(Level.WARNING) &&
                !System.getProperty("java.vendor").contains("Sun Microsystems")) {
            logger.warning("This application is running on a Java runtime that neither supports the JSR-199 API " +
                    "nor is it distributed by Sun Microsystems. However, the compiler implementation that will " +
                    "be used depends on internal classes in the package 'com.sun.tools.javac' so compilation " +
                    "is likely to fail! Be sure that the Java runtime that you're using provides these internal " +
                    "classes!");
        }

        return new JavacCompiler();
    }
}