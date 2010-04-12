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
package org.apache.myfaces.extensions.scripting.core.classIdentifier;

import org.apache.myfaces.extensions.scripting.core.CoreWeaver;
import org.apache.myfaces.extensions.scripting.core.support.Consts;
import org.apache.myfaces.extensions.scripting.core.support.TWeavingContext;
import org.apache.myfaces.extensions.scripting.loaders.java.JavaScriptingWeaver;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author werpu
 */
public class JavaDynamicClassIdentifierTest {

    Object probe1 = null;
    Object probe2 = null;
    CoreWeaver weaver = null;
    private static final String CLASS_SHOULD_BE_STATIC = "Class should be static";
    private static final String CLASS_SHOULD_BE_DYNAMIC = "Class should be dynamic";

    @Before
    public void setUp() {

        probe1 = new Probe1();
        URL rootPath = this.getClass().getClassLoader().getResource(".");

        DynamicClassloader throwAwayClassloader = new DynamicClassloader(this.getClass().getClassLoader(), rootPath.getPath());

        try {
            probe2 = throwAwayClassloader.loadClass(Consts.PROBE2, false).newInstance();
        } catch (Throwable e) {
            fail(e.getMessage());
        }

        weaver = new CoreWeaver(new JavaScriptingWeaver());
        TWeavingContext.setScriptingEnabled(true);
        TWeavingContext.setWeaverForTesting(weaver);
    }

    @Test
    public void isStatic() {
        assertFalse(CLASS_SHOULD_BE_STATIC, TWeavingContext.isDynamic(probe1.getClass()));
    }

    @Test
    public void isDynamic() {
        assertTrue(CLASS_SHOULD_BE_DYNAMIC, TWeavingContext.isDynamic(probe2.getClass()));
    }

}
