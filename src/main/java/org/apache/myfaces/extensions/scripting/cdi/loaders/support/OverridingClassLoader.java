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
package org.apache.myfaces.extensions.scripting.cdi.loaders.support;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>A reloadable class loader implementation that you can use to forcefully reload classes
 * even if you don't want to recompile them and hence haven't got the actual .class file. Use
 * this class loader if you want to reload a class that depends on a dynamically compiled
 * class, for example, in case of Spring if you've got a factory bean constructing bean
 * instances of a dynamically compiled class. Once the dynamically compiled class changes,
 * the class of the factory bean has to be reloaded as well even though it somehow didn't
 * really change.</p>
 */
public class OverridingClassLoader extends AbstractThrowAwayClassLoader {

    // ------------------------------------------ Constructors

    /**
     * <p>Constructs a new overriding class loader using the name of the class that
     * it's going to override and the parent class loader. Note that this class loader
     * only loads the class definition for the given class name. Otherwise it will
     * delegate to the parent class loader.</p>
     *
     * @param className the name of the class that it's going to override
     * @param parent    the parent class loader
     */
    public OverridingClassLoader(String className, ClassLoader parent) {
        super(className, parent);
    }

    // ------------------------------------------ AbstractThrowAwayClassLoader methods

    /**
     * <p>Opens a stream to the resource that defines the given class using the parent
     * class loader. If it cannot be found, return <code>null</code>.</p>
     *
     * @param className the class to load
     * @return a stream to the resource that defines the given class
     * @throws java.io.IOException if an I/O error occurs
     */
    protected InputStream openStreamForClass(String className) throws IOException {
        return getParent().getResourceAsStream(className.replace('.', '/') + ".class");
    }

}