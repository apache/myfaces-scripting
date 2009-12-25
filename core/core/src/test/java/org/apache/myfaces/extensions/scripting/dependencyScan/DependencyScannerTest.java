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
package org.apache.myfaces.extensions.scripting.dependencyScan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.core.dependencyScan.ClassDependencies;
import org.apache.myfaces.scripting.core.dependencyScan.DefaultDependencyScanner;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class DependencyScannerTest {

    Log log = LogFactory.getLog(DependencyScannerTest.class.getName());
    private static final String PROBE1 = "org.apache.myfaces.extensions.scripting.dependencyScan.probes.Probe";
    private static final String PROBE2 = "org.apache.myfaces.extensions.scripting.dependencyScan.probes.Probe2";
    private static final String PROBE3 = "org.apache.myfaces.extensions.scripting.dependencyScan.probes.Probe3";
    private static final String PROBE4 = "org.apache.myfaces.extensions.scripting.dependencyScan.probes.Probe4";
    private static final String PROBE_PAR = "org.apache.myfaces.extensions.scripting.dependencyScan.probes.ProbeParent";
    private static final String STRING = "java.lang.String";
    private static final String DUMMY = "org.apache.xxx";
    private static final String PROBE_NAMESPACE = "org.apache.myfaces.extensions.scripting";

    @Test
    public void testScan() {
        Set<String> whiteList = new HashSet<String>();
        whiteList.add(DUMMY);
        whiteList.add(PROBE_NAMESPACE);

        long before = System.currentTimeMillis();

        Set<String> retVal = (new DefaultDependencyScanner()).fetchDependencies(Thread.currentThread().getContextClassLoader(), PROBE1, whiteList);
        long after = System.currentTimeMillis();

        log.info("execution time" + (after - before));

        assertTrue(retVal.size() > 0);

        assertFalse(retVal.contains(STRING));

        assertTrue(retVal.contains(PROBE2));
        assertTrue(retVal.contains(PROBE3));
        assertTrue(retVal.contains(PROBE4));
        assertTrue(retVal.contains(PROBE_PAR));

    }

    public void testClassDependencies() {
        Set<String> whiteList = new HashSet<String>();
        whiteList.add(DUMMY);
        whiteList.add(PROBE_NAMESPACE);

        Set<String> retVal = (new DefaultDependencyScanner()).fetchDependencies(Thread.currentThread().getContextClassLoader(), PROBE1, whiteList);
        ClassDependencies dependencyMap = new ClassDependencies();

        dependencyMap.addDependencies(PROBE1, retVal);

        assertTrue("Dependency Test1", dependencyMap.getReferencedClasses(PROBE2).contains(PROBE1));
        assertTrue("Dependency Test2", dependencyMap.getReferencedClasses(PROBE3).contains(PROBE1));
        assertTrue("Dependency Test3", dependencyMap.getReferencedClasses(PROBE4).contains(PROBE1));
        assertTrue("Dependency Test4", dependencyMap.getReferencedClasses(PROBE_PAR).contains(PROBE1));

    }

}
