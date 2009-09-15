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
package org.apache.myfaces.scripting.core.util;

import static org.junit.Assert.*;
import static org.apache.myfaces.scripting.core.util.ReflectUtil.*;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ReflectUtilTest {

    TestProbe probe = new TestProbe();

    @Test
    public void executeMethod1() {
        executeMethod(probe, "testMethod1");

        try {
            executeMethod(probe, "testMethod2");
            fail("method2 throws an internal error");
        } catch (RuntimeException ex) {
        }

    }

    @Test
    public void executeMethod2() {
        try {
            executeMethod(probe, "testMethod3");
        } catch (RuntimeException e) {

        }

        try {
            executeMethod(probe, "testMethod3", 10);
        } catch (RuntimeException e) {

        }

        boolean retVal = (Boolean) executeMethod(probe, "testMethod3", "hello world");
        assertTrue(retVal);
    }

    @Test
    public void executeStatic() {
        boolean retVal = (Boolean) executeStaticMethod(TestProbe.class, "testMethod4", "1", "2");
    }

}
