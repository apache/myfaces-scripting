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
package org.apache.myfaces.extensions.scripting.core.dependencyScan.api;

/**
 * Generic filter pattern interface
 * used by our dependency registry to pre-filter the classes
 */
public interface ClassFilter {

    /**
     * checks whether the class is allowed to be processed by the filter or not
     *
     * @param engineType integer value of the engine type of the class
     * @param clazz      the class itself to be processed by the filter
     * @return true if it is allowed to be processed false otherwise
     */
    public boolean isAllowed(Integer engineType, String clazz);
}
