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

package org.apache.myfaces.scripting.loaders.java;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.support.ContextUtils;
import org.apache.myfaces.scripting.core.support.PathUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.io.File;
import java.security.AccessController;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class RecompiledClassLoaderTest {

    private static final PathUtils _pathUtils = new PathUtils();
    private static final String RESOURCES = _pathUtils.getResource(".");
    Logger logger = Logger.getLogger(RecompiledClassLoaderTest.class.getName());

    ServletContext _context;
    RecompiledClassLoader _loader;
    File _root = new File(RESOURCES + File.separator + "compiler/");

    @Before
    public void init() throws Exception {
        _context = ContextUtils.startupSystem();
        WeavingContext.getConfiguration().getSourceDirs(ScriptingConst.ENGINE_TYPE_JSF_JAVA).clear();
        WeavingContext.getConfiguration().addSourceDir(ScriptingConst.ENGINE_TYPE_JSF_JAVA, _root.getAbsolutePath());

        _loader = new RecompiledClassLoader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, ".java", false);
        ContextUtils.doJavaRecompile(_root.getAbsolutePath());

    }

    @Test
    public void testInit2() {
        new RecompiledClassLoader();
    }

    @Test
    public void testLoadClass() throws Exception {

        //the simple reloading case is handled by the reloading strategy testcase
        //so this test is mostly just to test the rest of the class

        Class clazz1 = _loader.loadClass("java.lang.String");
        Class clazz2 = _loader.loadClass("java.lang.String");
        assertTrue(clazz1.hashCode() == clazz2.hashCode());

    }

    @Test
    public void testGetSetSourceRoot() throws Exception {
        _loader.setSourceRoot("booga");
        assertTrue(_loader.getSourceRoot().equals("booga"));
    }

}
