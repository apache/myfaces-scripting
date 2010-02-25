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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>Test class for
 * <code>org.apache.myfaces.extensions.scripting.loader.dependencies.registry.DefaultDependencyRegistry</code>.</p>
 *
 * @author Bernhard Huemer
 */
public class DefaultDependencyRegistryTest {

    // ------------------------------------------ Test methods

    /**
     * <p>Tests whether the registry stores dependencies and dependent classes correctly. Just
     * consider this test case as some kind of example of what I mean with "dependent classes"
     * and "dependencies".</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRegisterDependencies() throws Exception {
        DefaultDependencyRegistry registry = new DefaultDependencyRegistry();
        registry.registerDependency("com.foo.Bar", "com.foo.Bla");
        registry.registerDependency("com.foo.Bar", "com.foo.Blubb");

        assertEquals(1, registry.getDependentClasses("com.foo.Bla").size());
        assertTrue(registry.getDependentClasses("com.foo.Bla").contains("com.foo.Bar"));

        assertEquals(1, registry.getDependentClasses("com.foo.Blubb").size());
        assertTrue(registry.getDependentClasses("com.foo.Blubb").contains("com.foo.Bar"));

        assertEquals(0, registry.getDependentClasses("com.foo.Bar").size());
    }

}
