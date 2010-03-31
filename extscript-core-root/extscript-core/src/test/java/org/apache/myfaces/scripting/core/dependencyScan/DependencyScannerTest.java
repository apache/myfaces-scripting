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
package org.apache.myfaces.scripting.core.dependencyScan;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.dependencyScan.StandardDependencyScanner;
import org.apache.myfaces.scripting.core.dependencyScan.core.ClassDependencies;
import org.apache.myfaces.scripting.core.dependencyScan.filter.WhitelistFilter;
import org.apache.myfaces.scripting.core.dependencyScan.registry.DependencyRegistryImpl;
import org.apache.myfaces.scripting.core.dependencyScan.registry.ExternalFilterDependencyRegistry;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class DependencyScannerTest {

    Logger log = Logger.getLogger(DependencyScannerTest.class.getName());

    private static final String PROBE1 = "org.apache.myfaces.scripting.core.dependencyScan.probes.Probe";
    private static final String PROBE2 = "org.apache.myfaces.scripting.core.dependencyScan.probes.Probe2";
    private static final String PROBE3 = "org.apache.myfaces.scripting.core.dependencyScan.probes.Probe3";
    private static final String PROBE4 = "org.apache.myfaces.scripting.core.dependencyScan.probes.Probe4";
    private static final String GENERICS_PROBE = "org.apache.myfaces.scripting.core.dependencyScan.probes.GenericsProbe";

    private static final String PROBE_PAR = "org.apache.myfaces.scripting.core.dependencyScan.probes.ProbeParent";
    private static final String DUMMY = "org.apache.xxx";
    private static final String PROBE_NAMESPACE = "org.apache.myfaces.scripting.core.dependencyScan";

    /**
     * Basic dependency scanning test, which tests
     * for basic intra class relationships
     */
    @Test
    public void testClassDependencies2() {
        ClassDependencies dependencyMap = new ClassDependencies();
        ExternalFilterDependencyRegistry testRegistry = new DependencyRegistryImpl(ScriptingConst.ENGINE_TYPE_JSF_JAVA, dependencyMap);
        testRegistry.addFilter(new WhitelistFilter(DUMMY, PROBE_NAMESPACE));
        long before = System.currentTimeMillis();
        (new StandardDependencyScanner()).fetchDependencies(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, PROBE1, testRegistry);
        long after = System.currentTimeMillis();
        log.info("Execution time registry based scan" + (after - before));

        assertTrue("Dependency Test1", dependencyMap.getReferringClasses(PROBE2).contains(PROBE1));
        assertTrue("Dependency Test2", dependencyMap.getReferringClasses(PROBE3).contains(PROBE1));
        assertTrue("Dependency Test3", dependencyMap.getReferringClasses(PROBE4).contains(PROBE1));
        assertTrue("Dependency Test4", dependencyMap.getReferringClasses(PROBE_PAR).contains(PROBE1));

    }

    /**
     * Test for
     * https://issues.apache.org/jira/browse/EXTSCRIPT-70
     */
    @Test
    public void testGenerics() {
        ClassDependencies dependencyMap = new ClassDependencies();
        ExternalFilterDependencyRegistry testRegistry = new DependencyRegistryImpl(ScriptingConst.ENGINE_TYPE_JSF_JAVA, dependencyMap);
        testRegistry.addFilter(new WhitelistFilter(DUMMY, PROBE_NAMESPACE));
        long before = System.currentTimeMillis();
        (new StandardDependencyScanner()).fetchDependencies(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, GENERICS_PROBE, testRegistry);
        long after = System.currentTimeMillis();
        log.info("Execution time registry based scan" + (after - before));

        assertTrue("GenericsDependencyTest 1", dependencyMap.getReferringClasses(PROBE1).contains(GENERICS_PROBE));
        assertTrue("GenericsDependencyTest 2", dependencyMap.getReferringClasses(PROBE2).contains(GENERICS_PROBE));
    }
}
