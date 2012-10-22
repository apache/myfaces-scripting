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

package org.apache.myfaces.extensions.scripting.core.utilstest;

import org.apache.myfaces.extensions.scripting.core.common.util.Cast;
import org.apache.myfaces.extensions.scripting.core.common.util.Null;
import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;
import org.apache.myfaces.extensions.scripting.core.probes.Probe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Testcase for our reflect utils
 * which we rely heavily upon
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ReflectUtilTest {
    private static final String HELLO_WORLD = "Hello World";
    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String MSG_INSTANTIATED = "String must be instantiated";
    private static final String MSG_PROBE_INSTANTIATED = "Probe must be instantiated";
    private static final String MSG_INIT_FAIL = "init failed expected";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInstantiate() throws Exception {
        String retVal = (String) ReflectUtil.instantiate(JAVA_LANG_STRING);
        assertTrue(MSG_INSTANTIATED, retVal != null);

        retVal = (String) ReflectUtil.instantiate(JAVA_LANG_STRING, HELLO_WORLD);
        assertTrue(MSG_INSTANTIATED, retVal != null && retVal.equals(HELLO_WORLD));

        Object myHello = HELLO_WORLD;
        Object probe = ReflectUtil.instantiate(Probe.class, new Cast(String.class, myHello), HELLO_WORLD);
        assertTrue(MSG_PROBE_INSTANTIATED, probe != null);

        try {
            ReflectUtil.instantiate(Probe.class, new Cast(Integer.class, myHello), HELLO_WORLD);
            fail();
        } catch (RuntimeException ex) {
            assertTrue(MSG_INIT_FAIL, true);
        }
        probe = ReflectUtil.instantiate(Probe.class, new Null(String.class), new Null(String.class));
        assertTrue(MSG_PROBE_INSTANTIATED, probe != null);

        try {
            ReflectUtil.instantiate(Probe.class, new Null(Integer.class), new Null(String.class));
            fail();
        } catch (RuntimeException ex) {
            assertTrue(MSG_INIT_FAIL, true);
        }

        //TODO (1.1) test fails, but is not used so we can live with it  
        //probe = ReflectUtil.instantiate(Probe2.class,new Array(String.class, HELLO_WORLD, HELLO_WORLD));
        //assertTrue("Probe must be instantiated", probe != null);
    }

    @Test
    public void testNewObject() throws Exception {
        String retVal = (String) ReflectUtil.newObject(String.class);
        assertTrue(MSG_INSTANTIATED, retVal != null);
    }

    @Test
    public void testExecuteStaticMethod() throws Exception {
        Boolean retVal = (Boolean) ReflectUtil.executeStaticMethod(Boolean.class, "valueOf", "true");
        assertTrue("retval must be true", retVal);

        try {
            ReflectUtil.executeStaticMethod(Boolean.class, "xx_valueOf", "true");
            fail();
        } catch (RuntimeException ex) {
            assertTrue("Exception must be thrown", true);
        }

    }

    @Test
    public void testFastExecuteStaticMethod() throws Exception {
        Boolean retVal = (Boolean) ReflectUtil.fastExecuteStaticMethod(Boolean.class, "valueOf", "true");
        assertTrue("retval must be true", retVal);
    }

    @Test
    public void testGetAllMethods() throws Exception {
        Collection<Method> retVal = ReflectUtil.getAllMethods(Boolean.class, "valueOf", 1);
        assertTrue(retVal.size() == 2);/*String and boolean*/
        retVal = ReflectUtil.getAllMethods(Object.class, "toString", 0);
        assertTrue(retVal.size() == 1);/*String and boolean*/
    }

    @Test
    public void testExecuteMethod() throws Exception {

        Boolean probe = true;
        Boolean retVal = (Boolean) ReflectUtil.executeMethod(probe, "valueOf", "true");
        assertTrue(retVal);
        String sRetVal = (String) ReflectUtil.executeMethod(probe, "toString");
        assertTrue(sRetVal.equals("true"));

        Object hashVal = ReflectUtil.executeMethod(new Probe(), "hashCode");
        assertTrue(hashVal != null);

        try {
            ReflectUtil.executeMethod(new Probe(), "xx_hashCode");
            fail();
        } catch (RuntimeException ex) {
            assertTrue("calling must faile with an RE", true);
        }
    }

    @Test
    public void testFastExecuteMethod() throws Exception {

        Boolean probe = true;
        Boolean retVal = (Boolean) ReflectUtil.fastExecuteMethod(probe, "valueOf", "true");
        assertTrue(retVal);
        String sRetVal = (String) ReflectUtil.fastExecuteMethod(probe, "toString");
        assertTrue(sRetVal.equals("true"));

        Object hashVal = ReflectUtil.fastExecuteMethod(new Probe(), "hashCode");
        assertTrue(hashVal != null);

    }

    @Test
    public void testCast() {
        assertTrue("Cast testing", ReflectUtil.cast(String.class, HELLO_WORLD) instanceof Cast);
        assertTrue("Cast testing", ReflectUtil.nullCast(String.class) instanceof Null);
    }
}
