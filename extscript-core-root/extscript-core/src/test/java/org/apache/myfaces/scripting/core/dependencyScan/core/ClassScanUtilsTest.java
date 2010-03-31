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

package org.apache.myfaces.scripting.core.dependencyScan.core;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ClassScanUtilsTest {
    @Test
    public void testIsStandardNamespace() throws Exception {
        new ClassScanUtils();//to reduce the dummy line coverage
        assertTrue(ClassScanUtils.isStandardNamespace("java.lang.String"));
        assertTrue(ClassScanUtils.isStandardNamespace("javax.faces"));
        assertTrue(ClassScanUtils.isStandardNamespace("com.sun"));
        assertTrue(ClassScanUtils.isStandardNamespace("org.jboss"));
        assertTrue(ClassScanUtils.isStandardNamespace("org.junit"));
        assertTrue(ClassScanUtils.isStandardNamespace("org.netbeans"));
        assertTrue(ClassScanUtils.isStandardNamespace("groovy.lang"));
        assertTrue(ClassScanUtils.isStandardNamespace("scala.lang"));
        assertTrue(ClassScanUtils.isStandardNamespace("org.springframework"));
        assertTrue(ClassScanUtils.isStandardNamespace("org.eclipse"));
        assertFalse(ClassScanUtils.isStandardNamespace("org.myproject"));
        assertFalse(ClassScanUtils.isStandardNamespace("groovy"));
    }

    
}
