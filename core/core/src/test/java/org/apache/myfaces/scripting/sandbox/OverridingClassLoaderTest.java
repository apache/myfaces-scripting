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
package org.apache.myfaces.scripting.sandbox;

import junit.framework.TestCase;
import org.apache.myfaces.scripting.sandbox.loader.support.OverridingClassLoader;

/**
 * <p>Test class for the class
 * <code>org.apache.myfaces.scripting.sandbox.loader.support.OverridingClassLoader</code></p>
 */
public class OverridingClassLoaderTest extends TestCase {

    // ------------------------------------------ Test methods

    /**
     * <p>Tests whether it is possible to override class definitions using the OverridingClassLoader,
     * i.e. it tests if you can produce multiple different Class objects using different class loaders
     * (which is not really a surprising thing at all).</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    public void testOverrideClass() throws Exception {
        ClassLoader classLoader = new OverridingClassLoader(
                Dummy.class.getName(), OverridingClassLoaderTest.class.getClassLoader());

        Class dummyClass = classLoader.loadClass(Dummy.class.getName());
        assertNotSame("The OverridingClassLoader didn't return a different Class instance.",
                Dummy.class, dummyClass);

        // .. and another time
        classLoader = new OverridingClassLoader(Dummy.class.getName(), classLoader);
        Class secondDummyClass = classLoader.loadClass(Dummy.class.getName());
        assertNotSame("The OverridingClassLoader didn't return a different Class instance.",
                Dummy.class, secondDummyClass);
        assertNotSame("The OverridingClassLoader didn't return a different Class instance.",
                dummyClass, secondDummyClass);
    }

    /**
     * <p>Tests whether the OverridingClassLoader delegates the parent class loader correctly.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    public void testOverrideDifferentClass() throws Exception {
        ClassLoader classLoader = new OverridingClassLoader(
                Dummy.class.getName(), OverridingClassLoaderTest.class.getClassLoader());
        assertSame("The OverridingClassLoader replaced a Class instance that he wasn't supposed to replace.",
                Object.class, classLoader.loadClass("java.lang.Object"));
    }

    // ------------------------------------------ Dummy classes

    /**
     * <p>This class will be reloaded in some test cases.</p>
     */
    private static class Dummy {

    }

}
