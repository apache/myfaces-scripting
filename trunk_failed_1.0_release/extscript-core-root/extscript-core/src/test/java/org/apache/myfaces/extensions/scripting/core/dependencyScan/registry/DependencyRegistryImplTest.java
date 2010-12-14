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

package org.apache.myfaces.extensions.scripting.core.dependencyScan.registry;

import org.apache.myfaces.extensions.scripting.core.dependencyScan.registry.DependencyRegistryImpl;
import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.core.ClassDependencies;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class DependencyRegistryImplTest {

    ClassDependencies _classDeps;
    DependencyRegistry _dependencyRegistry;

    @Before
    public void init() {
        _classDeps = new ClassDependencies();
        _dependencyRegistry = new DependencyRegistryImpl(ScriptingConst.ENGINE_TYPE_JSF_JAVA, _classDeps);
    }

    @Test
    public void testClearFilters() throws Exception {
        ((DependencyRegistryImpl) _dependencyRegistry).clearFilters();
        assertFalse("standard namespace must be set after clear", ((DependencyRegistryImpl) _dependencyRegistry).isAllowed(ScriptingConst.ENGINE_TYPE_JSF_JAVA, "java.lang.String"));
    }

    @Test

    public void testAddDependency() throws Exception {
        _dependencyRegistry.addDependency(ScriptingConst.ENGINE_TYPE_JSF_JAVA, null, null, null);
        _dependencyRegistry.addDependency(ScriptingConst.ENGINE_TYPE_JSF_JAVA, null, "", "");

        //The rest is covered by other tests

    }

    @Test
    public void testFlush() throws Exception {
        ((DependencyRegistryImpl) _dependencyRegistry).flush(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
        assertFalse("standard namespace must be set after clear", ((DependencyRegistryImpl) _dependencyRegistry).isAllowed(ScriptingConst.ENGINE_TYPE_JSF_JAVA, "java.lang.String"));
    }
}
