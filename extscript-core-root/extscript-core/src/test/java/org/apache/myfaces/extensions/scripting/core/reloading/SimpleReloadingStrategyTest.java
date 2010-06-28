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

package org.apache.myfaces.extensions.scripting.core.reloading;

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.api.ScriptingWeaver;
import org.apache.myfaces.extensions.scripting.core.probes.Probe;
import org.apache.myfaces.extensions.scripting.core.support.ContextUtils;
import org.apache.myfaces.extensions.scripting.core.support.ObjectReloadingWeaver;
import org.apache.myfaces.extensions.scripting.core.support.PathUtils;
import org.apache.myfaces.extensions.scripting.core.support.TestingJavaScriptingWeaver;
import org.apache.myfaces.extensions.scripting.core.util.ReflectUtil;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.extensions.scripting.loaders.java.RecompiledClassLoader;
import org.apache.myfaces.extensions.scripting.monitor.RefreshAttribute;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Simple Reloading Strategy
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class SimpleReloadingStrategyTest {

    private static final PathUtils _pathUtils = new PathUtils();
    private static final String RESOURCES = _pathUtils.getResource(".");
    Logger logger = Logger.getLogger(SimpleReloadingStrategyTest.class.getName());

    ServletContext _context;
    RecompiledClassLoader _loader;
    File _root = new File(RESOURCES + File.separator + "compiler/");

    SimpleReloadingStrategy _strategy;

    static class MyProbe {
        //weaver = new CoreWeaver(new JavaScriptingWeaver());
        //TWeavingContext.setScriptingEnabled(true);
        //TWeavingContext.setWeaverForTesting(weaver);
    }

    @Before
    public void init() throws Exception {
        _context = ContextUtils.startupSystem();
        WeavingContext.getConfiguration().getSourceDirs(ScriptingConst.ENGINE_TYPE_JSF_JAVA).clear();
        WeavingContext.getConfiguration().addSourceDir(ScriptingConst.ENGINE_TYPE_JSF_JAVA, _root.getAbsolutePath());

        _loader = new RecompiledClassLoader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, ".java", false);
        ContextUtils.doJavaRecompile(_root.getAbsolutePath());

        /**
         * we now work on our normal scripting weaver for java for this testcase
         */
        WeavingContext.setWeaver(new TestingJavaScriptingWeaver());
        _strategy = new SimpleReloadingStrategy(WeavingContext.getWeaver());
    }

    @Test
    public void testReload() throws Exception {
        synchronized (ContextUtils.COMPILE_LOAD_MONITOR) {
            Object probe = _loader.loadClass("compiler.TestProbe1").newInstance();
            RefreshAttribute metaData = getMetadata(probe);
            WeavingContext.getRefreshContext().getDaemon().getClassMap().put("compiler.TestProbe1", metaData);

            ReflectUtil.executeMethod(probe, "setTestAttr", "hello");
            Object probe2 = _strategy.reload(probe, ScriptingConst.ENGINE_TYPE_JSF_JAVA);
            Object attr = ReflectUtil.executeMethod(probe, "getTestAttr");
            assertFalse(probe.hashCode() == probe2.hashCode());
            assertTrue(attr instanceof String);
            assertTrue(((String) attr).equals("hello"));

            Object probe3 = _strategy.reload(probe2, ScriptingConst.ENGINE_TYPE_JSF_JAVA);
            assertTrue(probe2 == probe3);
        }
    }

    private RefreshAttribute getMetadata(Object probe) {
        RefreshAttribute metaData = new RefreshAttribute();
        metaData.setAClass(probe.getClass());
        metaData.setSourcePath(RESOURCES);
        metaData.setFileName("compiler/TestProbe1.java");
        metaData.setScriptingEngine(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
        metaData.requestRefresh();
        metaData.setTimestamp(System.currentTimeMillis());
        return metaData;
    }

    @Test
    public void testGetSetWeaver() throws Exception {
        ScriptingWeaver weaver = new ObjectReloadingWeaver(Probe.class);
        _strategy.setWeaver(weaver);
        assertTrue(_strategy.getWeaver() == weaver);
    }
}
