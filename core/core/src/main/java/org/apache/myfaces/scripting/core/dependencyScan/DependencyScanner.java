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
package org.apache.myfaces.scripting.core.dependencyScan;

import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A dependency scanner for
 *          our classes
 *          <p/>
 *          The idea beind it is that a dependency scanner
 *          should scan loaded classes for their dependencies
 *          into a whitelist of packages, a dynamically loaded class
 *          then now can taint other classes if altered
 *          which are in the whitelist so that those artefacts get reloaded
 *          <p/>
 *          The whitelist itself for now should only be
 *          classes from dynamically loaded packages
 */
public interface DependencyScanner {

    /**
     * fetch the dependencies from a given classname and
     * register them in a registry
     *
     * @param loader    the classloader responsible for serving the infrastructure
     * @param className the classname from which the dependencies have to be fetched
     * @param registry  our registry which should store the dependencies
     */
    public void fetchDependencies(ClassLoader loader, String className, DependencyRegistry registry);

    /**
     * soon to be deprecated, fetch dependencies working on an existing whitelist system
     *
     * @param loader
     * @param className
     * @param whiteList
     * @return a list of dependencies from the class &lt;className&gt;
     */
    public Set<String> fetchDependencies(ClassLoader loader, String className, Set<String> whiteList);
}
