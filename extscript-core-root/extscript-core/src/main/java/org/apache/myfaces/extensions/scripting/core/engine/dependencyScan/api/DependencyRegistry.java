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
package org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api;

/**
 * General contractual interface for a dependency registry
 * The dependency registry is a class which stores dependencies
 * according to an internal whitelisting system.
 * <p/>
 * Only classes which pass the whitelisting check will be processed
 */
public interface DependencyRegistry {
    /**
     * adds a source dependency if it is able to pass the
     * filters
     * A dependency is only allowed to pass if it is able
     * to pass the internal filter list
     *
     * @param engineType            the engine type for this dependency
     * @param rootClass             the root class of this scan which all dependencies are referenced from
     * @param currentlyVisitedClass the source which includes or casts the dependencies
     * @param dependency            the dependency to be added
     */
    void addDependency(Integer engineType, String rootClass, String currentlyVisitedClass, String dependency);

    /**
     * Flush which is issued at the end of processing to flush
     * any content which has not been yet processed into our content holding
     * data structures
     *
     * @param engineType the engine type which has issued the flush operation
     */
    void flush(Integer engineType);
}
