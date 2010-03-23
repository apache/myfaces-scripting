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
package org.apache.myfaces.scripting.core.dependencyScan.registry;

import org.apache.myfaces.scripting.core.dependencyScan.api.DependencyRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A master dependency registry which keeps track of various
 * sub-registries in our dependency scanning system
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class MasterDependencyRegistry implements DependencyRegistry {

    /**
     * We keep our central registry in a map
     * with the engineType as key value to detect which
     * registry needs to be triggered
     */
    private Map<Integer, DependencyRegistry> _subRegistries = new ConcurrentHashMap<Integer, DependencyRegistry>();

    /**
     * adds a new dependency to all registered registries
     *
     * @param engineType   the engine type which holds the registry
     * @param rootClass    the root class of this scan which all dependencies are referenced from
     * @param currentClass the current class scanned
     * @param dependency   the dependency to be added relative to the current class
     */
    public void addDependency(Integer engineType, String rootClass, String currentClass, String dependency) {
        for (Map.Entry<Integer, DependencyRegistry> entry : _subRegistries.entrySet()) {
            entry.getValue().addDependency(engineType, rootClass, currentClass, dependency);
        }
    }

    /**
     * Flush which is issued at the end of processing to flush
     * any content which has not been yet processed into our content holding
     * data structures
     *
     * @param engineType the engine type which has issued the flush operation
     */
    public void flush(Integer engineType) {
        for (Map.Entry<Integer, DependencyRegistry> entry : _subRegistries.entrySet()) {
            entry.getValue().flush(engineType);
        }
    }

    /**
     * adds a subregistry to our current master registry
     *
     * @param engineType the engine type which is the key to our subregistry
     * @param registry   the subregistry which has to be added
     */
    public void addSubregistry(Integer engineType, DependencyRegistry registry) {
        _subRegistries.put(engineType, registry);
    }

    /**
     * Getter for getting a subregistry from our given registry
     *
     * @param engineType the engine type to search for
     * @return the subregistry according to the engine type, or null if none is found
     */
    public DependencyRegistry getSubregistry(Integer engineType) {
        return _subRegistries.get(engineType);
    }

}
