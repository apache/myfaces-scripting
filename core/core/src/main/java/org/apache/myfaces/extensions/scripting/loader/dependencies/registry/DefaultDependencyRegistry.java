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
package org.apache.myfaces.extensions.scripting.loader.dependencies.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Default implementation of a dependency registry.</p>
 *
 * @author Bernhard Huemer
 */
public class DefaultDependencyRegistry implements DependencyRegistry {

    /** The dependency map that maps from a class name to a set of classes that depend on this class. */
    private final Map<String, HashSet<String>> dependentClassMap = new HashMap<String, HashSet<String>>();

    // ------------------------------------------ DependencyRegistry methods

    /**
     * <p>Registers a class dependency for the given class. In doing so, you tell the reloading
     * facility to reload the given class if it's the case that this particular dependency has
     * changed.</p>
     *
     * @param className the name of the class for which you want to register a new dependency
     * @param dependency the class dependency, i.e. the class that the given class depends on
     */
    public void registerDependency(String className, String dependency) {
        synchronized (dependentClassMap) {
            Set<String> dependentClasses = dependentClassMap.get(dependency);
            if (dependentClasses == null) {
                dependentClasses = new HashSet<String>();
                dependentClassMap.put(dependency, (HashSet<String>) dependentClasses);
            }

            // Register the given class in the set of dependent classes
            dependentClasses.add(className);
        }
    }

    /**
     * <p>Removes all registered dependencies for the given class.</p>
     *
     * @param className the name of the class for which you want to unregister all dependencies
     */
    public void unregisterDependencies(String className) {
        synchronized (dependentClassMap) {
            dependentClassMap.remove(className);
        }
    }

    /**
     * <p>Returns a set of class names of the classes that depend on the given classes, if any.
     * If there are no dependent classes, this method will return an empty set.</p>
     *
     * @param className the name of class for which you want to retrieve the dependent classes
     *
     * @return a set of class names of the dependent classes
     */
    public Set<String> getDependentClasses(String className) {
        Set<String> dependentClasses;

        synchronized (dependentClassMap) {
            dependentClasses = dependentClassMap.get(className);
        }
        
        if (dependentClasses == null) {
            dependentClasses = new HashSet<String>();
        }

        return Collections.unmodifiableSet(dependentClasses);
    }

}
