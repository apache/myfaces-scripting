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

import org.apache.myfaces.scripting.api.BaseWeaver;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.probes.Probe;
import org.junit.Test;

import static org.apache.myfaces.scripting.api.
        ScriptingConst.*;
import static org.junit.Assert.assertTrue;

/**
 * A simple test to ensure that the managed bean reloading strategy
 * does not do anything
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ManagedBeanReloadingStrategyTest {

    static final int[] ARTIFACT_TYPES = {
            ARTIFACT_TYPE_UNKNOWN,
            ARTIFACT_TYPE_MANAGEDBEAN,
            ARTIFACT_TYPE_MANAGEDPROPERTY,
            ARTIFACT_TYPE_RENDERKIT,
            ARTIFACT_TYPE_VIEWHANDLER,
            ARTIFACT_TYPE_RENDERER,
            ARTIFACT_TYPE_COMPONENT,
            ARTIFACT_TYPE_VALIDATOR,
            ARTIFACT_TYPE_BEHAVIOR,
            ARTIFACT_TYPE_APPLICATION,
            ARTIFACT_TYPE_ELCONTEXTLISTENER,
            ARTIFACT_TYPE_ACTIONLISTENER,
            ARTIFACT_TYPE_VALUECHANGELISTENER,
            ARTIFACT_TYPE_CONVERTER,
            ARTIFACT_TYPE_LIFECYCLE,
            ARTIFACT_TYPE_PHASELISTENER,
            ARTIFACT_TYPE_FACESCONTEXT,
            ARTIFACT_TYPE_NAVIGATIONHANDLER,
            ARTIFACT_TYPE_RESPONSEWRITER,
            ARTIFACT_TYPE_RESPONSESTREAM,
            ARTIFACT_TYPE_RESOURCEHANDLER,
            ARTIFACT_TYPE_CLIENTBEHAVIORRENDERER,
            ARTIFACT_TYPE_SYSTEMEVENTLISTENER,
    };

    static final class DummyWeaver extends BaseWeaver {

        @Override
        public boolean isDynamic(Class clazz) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void scanForAddedClasses() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected DynamicCompiler instantiateCompiler() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected String getLoadingInfo(String file) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Test
    public void testReload() throws Exception {
        Probe probe = new Probe();
        ManagedBeanReloadingStrategy strategy = new ManagedBeanReloadingStrategy();
        for (int artifactType : ARTIFACT_TYPES) {
            Object probe2 = strategy.reload(probe, artifactType);
            assertTrue(probe2 == probe);
        }
    }

    @Test
    public void testSetGetWeaver() throws Exception {
        ManagedBeanReloadingStrategy strategy = new ManagedBeanReloadingStrategy();
        ScriptingWeaver dummyWeaver = new DummyWeaver();
        strategy.setWeaver(dummyWeaver);
        assertTrue(strategy.getWeaver() == dummyWeaver);
    }

}
