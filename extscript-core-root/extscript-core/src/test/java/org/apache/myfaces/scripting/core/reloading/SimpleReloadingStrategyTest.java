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

package org.apache.myfaces.scripting.core.reloading;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.probes.Probe;
import org.apache.myfaces.scripting.core.support.ObjectReloadingWeaver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Simple Reloading Strategy
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class SimpleReloadingStrategyTest {

    SimpleReloadingStrategy _strategy;

    static class MyProbe {
        //weaver = new CoreWeaver(new JavaScriptingWeaver());
        //TWeavingContext.setScriptingEnabled(true);
        //TWeavingContext.setWeaverForTesting(weaver);
    }

    @Before
    public void init() {
        _strategy = new SimpleReloadingStrategy(new ObjectReloadingWeaver(Probe.class));
    }

    @Test
    public void testReload() throws Exception {
        //TODO we have to make a real class reloading here
        //be registering our probe again in a temporary classloader

       // Probe probe = new Probe();
       // Probe probe2 = (Probe) _strategy.reload(probe, ScriptingConst.ENGINE_TYPE_JSF_JAVA);

       // assertFalse(probe == probe2);
    }

    @Test
    public void testGetSetWeaver() throws Exception {
        ScriptingWeaver weaver = new ObjectReloadingWeaver(Probe.class);
        _strategy.setWeaver(weaver);
        assertTrue(_strategy.getWeaver() == weaver);
    }
}
