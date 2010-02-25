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
package org.apache.myfaces.extensions.scripting.loader;

import org.apache.myfaces.extensions.scripting.loader.dependencies.registry.DefaultDependencyRegistry;
import org.apache.myfaces.extensions.scripting.loader.dependencies.registry.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.loader.dependencies.scanner.DependencyScanner;

import java.io.File;

/**
 * 
 */
public class DependencyAwareReloadingClassLoader extends ReloadingClassLoader {

    /** The dependency scanner that determines dependencies for a class. */ 
    private DependencyScanner scanner;

    /** The dependency registry that keeps track of the dependencies determine by the scanner. */
    private DependencyRegistry registry;

    // ------------------------------------------ Constructors

    /**
     * <p>Constructs a new dependency aware reloading class loader for the specified
     * compilation directory using the default delegation parent class loader. Note
     * that this class loader will only delegate to the parent class loader if
     * there's no dynamically compiled class available.</p>
     *
     * <p>The given dependency scanner will be used to determine dependencies for each
     * class that this class loader has to load. If you then reload one of those
     * dependencies, the dependent class will be reloaded automatically as well.</p>
     *
     * @param scanner the dependency scanner that will be used to determine dependencies
     * @param compilationDirectory the compilation directory
     */
    public DependencyAwareReloadingClassLoader(DependencyScanner scanner, File compilationDirectory) {
        this(scanner, new DefaultDependencyRegistry(), compilationDirectory);
    }

    /**
     * <p>Constructs a new reloading class loader for the specified compilation
     * directory using the given delegation parent class loader. Note that this
     * class loader will only delegate to the parent class loader if there's no
     * dynamically compiled class available.</p>
     *
     * <p>The given dependency scanner will be used to determine dependencies for each
     * class that this class loader has to load. If you then reload one of those
     * dependencies, the dependent class will be reloaded automatically as well.</p>
     *
     * @param scanner the dependency scanner that will be used to determine dependencies
     * @param parentClassLoader    the parent class loader
     * @param compilationDirectory the compilation directory
     */
    public DependencyAwareReloadingClassLoader(DependencyScanner scanner, ClassLoader parentClassLoader, File compilationDirectory) {
        this(scanner, new DefaultDependencyRegistry(), parentClassLoader, compilationDirectory);
    }

    public DependencyAwareReloadingClassLoader(DependencyScanner scanner, DependencyRegistry registry, File compilationDirectory) {
        super(compilationDirectory);

        if (scanner == null) {
            throw new IllegalArgumentException("The given dependency scanner must not be null.");
        }

        this.scanner = scanner;

        if (registry != null) {
            this.registry = registry;
        } else {
            this.registry = new DefaultDependencyRegistry();
        }
    }

    public DependencyAwareReloadingClassLoader(DependencyScanner scanner, DependencyRegistry registry,
                                                ClassLoader parentClassLoader, File compilationDirectory) {
        super(parentClassLoader, compilationDirectory);

        if (scanner == null) {
            throw new IllegalArgumentException("The given dependency scanner must not be null.");
        }

        this.scanner = scanner;

        if (registry != null) {
            this.registry = registry;
        } else {
            this.registry = new DefaultDependencyRegistry();
        }
    }

    // ------------------------------------------ ReloadingClassLoader methods

    /**
     * <p>Reloads the given class internally explicitly. Note that this class loader usually
     * reloads classes automatically, i.e. this class loader detects if there is a newer
     * version of a class file available in the compilation directory. However, by using
     * this method you tell this class loader to forcefully reload the given class. For
     * example, if you've got a newer version of a dynamically recompiled class and a
     * statically compiled class depending on this one, you can tell this class loader to
     * reload the statically compiled class as well so that it references the correct
     * version of the Class object.</p>
     *
     * <p>This method also automatically reloads all dependent classes of the given class.</p>
     *
     * @param className the class you want to reload
     */
    @Override
    public void reloadClass(String className) {
        super.reloadClass(className);

        // First of all, update the dependencies of this class
        // as they could have changed due to the reload.
        registry.unregisterDependencies(className);
        scanner.scan(registry, this, className);

        // Reload all dependent classes as well, in doing so we're automatically updating
        // dependencies, even transitive ones. 
        for (String dependentClassName : registry.getDependentClasses(className)) {
            reloadClass(dependentClassName);
        }
    }

    /**
     * <p>Creates and returns new instance of a reloading class loader which is basically a clone of this one.</p>
     *
     */
    @Override
    protected ReloadingClassLoader cloneClassLoader(ClassLoader parentClassLoader, File compilationDirectory) {
        return new DependencyAwareReloadingClassLoader(
                scanner, registry, parentClassLoader, compilationDirectory);
    }
    
}
