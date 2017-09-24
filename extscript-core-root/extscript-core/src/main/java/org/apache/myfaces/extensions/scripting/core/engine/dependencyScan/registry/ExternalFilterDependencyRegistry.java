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
package org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.registry;

import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.ClassFilter;
import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.DependencyRegistry;

/**
 * General contractual interface for a dependency registry with external filters
 * being settable
 * <p>&nbsp;</p>
 * The dependency registry is a class which stores dependencies
 * according to an internal whitelisting system.
 * <p>&nbsp;</p>
 * Only classes which pass the whitelisting check will be processed
 */
public interface ExternalFilterDependencyRegistry extends DependencyRegistry
{

    /**
     * Clears the internal filters
     * for the registry
     */
    void clearFilters();

    /**
     * adds another filter to the internal filter list
     *
     * @param filter the filter to be added
     */
    void addFilter(ClassFilter filter);

    /**
     * Allowance check for external shortcutting
     * This check triggers into the internal filters
     * to pre-check if a class is allowed to pass or not
     *
     * @param className      the classname to be checked
     * @param engineType an identifier for the current scan type (jsf java scan for instance)
     * @return true if it is false otherwise
     */
    public boolean isAllowed(Integer engineType, String className);

    /**
     * Flush operation to batch sync
     * the current dependencies against a storage
     * <p>&nbsp;</p>
     * (will be removed later once we have all the code transitioned
     * to the registry system)
     */
    void flush(Integer engineType);
}
