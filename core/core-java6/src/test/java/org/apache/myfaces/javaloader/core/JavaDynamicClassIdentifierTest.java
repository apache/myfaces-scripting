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
package org.apache.myfaces.javaloader.core;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.apache.myfaces.scripting.core.CoreWeaver;
import org.apache.myfaces.scripting.loaders.java.JavaScriptingWeaver;


/**
 * @author werpu
 */

public class JavaDynamicClassIdentifierTest {

     Object probe1 = null;
     Object probe2 = null;
     CoreWeaver weaver = null;

     @Before
     public void setUp() {
        probe1 = new Probe1();
        probe2 = new Probe2();
        weaver = new CoreWeaver(new JavaScriptingWeaver());
        TWeavingContext.setWeaverForTesting(weaver);
    }


     @Test
     public void isDynamic() {
        assertFalse("Class should be static", TWeavingContext.isDynamic(probe1.getClass()));
        assertTrue("Class should be dynamic", TWeavingContext.isDynamic(probe2.getClass()));
     }



}
