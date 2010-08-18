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
package org.apache.myfaces.extensions.scripting.cdi.loaders.dependency.registry;

import java.util.Set;

/**
 * <p>A registry that keeps track of dependencies between classes. This enables you to
 * reload certain classes if one of its dependencies has changed, even if the dependent
 * class itself hasn't changed at all. However, once a dependency changes the dependent
 * class has to pick up those changes somehow.</p>
 *
 * @author Bernhard Huemer
 */
public interface DependencyRegistry {

    /**
     * <p>Registers a class dependency for the given class. In doing so, you tell the reloading
     * facility to reload the given class if it's the case that this particular dependency has
     * changed.</p>
     *
     * @param className the name of the class for which you want to register a new dependency
     * @param dependency the class dependency, i.e. the class that the given class depends on
     */
    public void registerDependency(String className, String dependency);

    /**
     * <p>Removes all registered dependencies for the given class.</p>
     *
     * @param className the name of the class for which you want to unregister all dependencies
     */
    public void unregisterDependencies(String className);

    /**
     * <p>Returns a set of class names of the classes that depend on the given classes, if any.
     * If there are no dependent classes, this method will return an empty set.</p>
     *
     * @param className the name of class for which you want to retrieve the dependent classes
     *
     * @return a set of class names of the dependent classes
     */
    public Set<String> getDependentClasses(String className);

}