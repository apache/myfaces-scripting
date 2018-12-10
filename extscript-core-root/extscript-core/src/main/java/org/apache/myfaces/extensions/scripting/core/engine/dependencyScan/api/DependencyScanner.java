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
 * Standard dependency scanner interface
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface DependencyScanner {
    /**
     * main method every dependency scanner has to implement
     *
     * @param loader     the classloader which is able to serve the requested class resources
     * @param engineType integer value of the scanning triggering engine type
     * @param className  of the class to be scanned
     * @param registry   the registry which should receive the results of the scan
     */
    public void fetchDependencies(ClassLoader loader, Integer engineType, String className, DependencyRegistry registry);
}
