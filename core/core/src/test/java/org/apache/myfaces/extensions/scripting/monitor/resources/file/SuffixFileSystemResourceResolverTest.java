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
package org.apache.myfaces.extensions.scripting.monitor.resources.file;

import org.apache.myfaces.extensions.scripting.monitor.resources.Resource;
import org.apache.myfaces.extensions.scripting.monitor.resources.ResourceResolver;
import org.apache.myfaces.extensions.scripting.monitor.resources.file.SuffixFileSystemResourceResolver;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * <p>Test class for
 * <code>org.apache.myfaces.extensions.scripting.monitor.resources.file.SuffixFileSystemResourceResolver</code>.</p>
 */
public class SuffixFileSystemResourceResolverTest {

    // ------------------------------------------ Test methods

    /**
     * <p>Tests whether the resource resolver only issues callbacks if it encountered a file that
     * ends with the given file suffix.</p>
     * 
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testScanClassFiles() throws Exception {
        ResourceResolver resourceResolver = new SuffixFileSystemResourceResolver(
                new File(getClass().getResource(".").getFile()), ".class");
        resourceResolver.resolveResources(new ResourceResolver.ResourceCallback() {
            public boolean handle(Resource resource) {
                assertTrue(String.format("The file [%s] doesn't end with '.class'.", resource.getFile()),
                        resource.getFile().getName().endsWith(".class"));
                return true;
            }
        });
    }

    /**
     * <p>Tests whether the resource resolver also issues callbacks if there are no such
     * files in the root directory. Note, the root directory is not empty, it's just the
     * case that we've used an arbitrary file suffix.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testScanNonExistingFiles() throws Exception {
        ResourceResolver resourceResolver = new SuffixFileSystemResourceResolver(
                new File(getClass().getResource(".").getFile()), ".foobar123??");
        resourceResolver.resolveResources(new ResourceResolver.ResourceCallback() {
            public boolean handle(Resource resource) {
                fail("There is no file that ends with '.foobar123??' so " +
                        "this callback shouldn't have been called.");
                return true;
            }
        });
    }

}
