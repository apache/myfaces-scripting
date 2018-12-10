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

package org.apache.myfaces.extensions.scripting.scanningcore.utilstest;

import org.junit.Before;
import org.junit.Test;
import org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import org.apache.myfaces.extensions.scripting.scanningcore.support.PathUtils;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for our FileUtils
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class FileUtilsTest {
    PathUtils pathUtils = new PathUtils();

    @Before
    public void init() {

    }

    @Test
    public void testForRegexp() {
        String fileSep = FileUtils.getFileSeparatorForRegex();
        assertTrue("must be double backslash instead of single one", (File.separator.equals("\\")) ? fileSep.equals("\\\\") : fileSep.equals(File.separator));
    }

    @Test
    public void testGetFileSeparator() {
        String fileSeparator = FileUtils.getFileSeparator();
        assertTrue(fileSeparator.equals(File.separator));
    }

    @Test
    public void testGetTempDir() {
        File tempDir = FileUtils.getTempDir();
        assertTrue(tempDir != null);
        assertTrue(tempDir.exists());
    }

    @Test
    public void testFileStrategy() {
        List<File> sourceFiles = FileUtils.fetchSourceFiles(new File(pathUtils.getResource("compiler/")), ".java");
        assertTrue("wildcarding is needed", sourceFiles.size() == 0);

        sourceFiles = FileUtils.fetchSourceFiles(new File(pathUtils.getResource("compiler/")), "java");
         assertTrue("wildcarding is needed", sourceFiles.size() == 0);


        sourceFiles = FileUtils.fetchSourceFiles(new File(pathUtils.getResource("compiler/")), "*.java");
        assertTrue("source files must have been found", sourceFiles.size() > 2);
        //check also for subdirs
        for(File sourceFile: sourceFiles) {
            if(sourceFile.getAbsolutePath().contains("myPackage")) {
                return;
            }
        }
        fail("source file must also be in myPackage");
    }

    @Test
    public void testDirStrategy() {
        StringBuilder result = FileUtils.fetchSourcePaths(new File(pathUtils.getResource("compilerx/")), "");
        assertTrue("invalid dir should result in empty results", result.toString().trim().length() == 0); 

        result = FileUtils.fetchSourcePaths(new File(pathUtils.getResource("compiler/")), "");
        assertTrue("myPackage should be found", result.toString().trim().contains("myPackage"));


    }

}
