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
package org.apache.myfaces.scripting.api;

/**
 * @author werpu
 * Generic  class identifier interface
 * has to be implemented by all identifiers
 */
public interface DynamicClassIdentifier {
   /**
     * identifies whether a given class is dynamic or not
    *
     * @param clazz the class which has to be investigates
     * @return true if it is dynamic false if not
     */
    public boolean isDynamic(Class clazz);

    /**
     * gets the engine 
     * @param clazz
     * @return
     */
    public int getEngineType(Class clazz);
}
