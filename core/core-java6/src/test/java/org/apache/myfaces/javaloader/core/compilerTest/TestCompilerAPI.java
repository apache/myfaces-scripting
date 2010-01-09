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
package org.apache.myfaces.javaloader.core.compilerTest;


import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.core.util.ReflectUtil;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class TestCompilerAPI {

    private static final String JAVA_FILE_ENDING = ".java";
    private static final String JSR199_COMPILER = "org.apache.myfaces.scripting.loaders.java.jsr199.JSR199Compiler";
    private static final String JAVA5_COMPILER = "org.apache.myfaces.scripting.loaders.java.jdk5.CompilerFacade";

    private static final String PROBE1 = "../../src/test/resources/compiler/TestProbe1.java";
    private static final String PROBE2 = "../../src/test/resources/compiler/TestProbe2.java";
    private static final String RESOURCES = "../../src/test/resources/";

    File probe1;
    File probe2;
    File root;

    public TestCompilerAPI() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //we use a location relative to our current root one to reach the sources
        //because the test also has to be performed outside of maven
        //and the ide cannot cope with resource paths for now
        String currentPath = loader.getResource("./").getPath();
        String sourcePath1 = currentPath + PROBE1;
        String sourcePath2 = currentPath + PROBE2;
        String rootPath = currentPath + RESOURCES;

        sourcePath1 = FilenameUtils.normalize(sourcePath1);
        sourcePath2 = FilenameUtils.normalize(sourcePath2);
        rootPath = FilenameUtils.normalize(rootPath);

        probe1 = new File(sourcePath1);
        probe2 = new File(sourcePath2);
        root = new File(rootPath);
    }


    private String getScriptingFacadeClass(boolean allowJSR199) {
        String javaVer = System.getProperty("java.version");
        String[] versionArr = javaVer.split("\\.");

        int major = Integer.parseInt(versionArr[Math.min(versionArr.length, 1)]);

        if (major > 5 && allowJSR199) {
            //jsr199 compliant jdk
            return JSR199_COMPILER;
        }
        //otherwise
        return JAVA5_COMPILER;
    }


    @Test
    public void testFullCompile() {
        File targetDir = null;
        try {


            DynamicCompiler compiler = (DynamicCompiler) ReflectUtil.instantiate(getScriptingFacadeClass(true));//new ReflectCompilerFacade();

            File target = compiler.compileAllFiles(root.getAbsolutePath(), "");

            assertTrue("target exists files are compiled into the target", target != null);
            File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1.class");
            File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2.class");

            assertTrue("Classfile1 is compiled into the target", classFile1.exists());
            assertTrue("Classfile2 is compiled into the target", classFile2.exists());
        } catch (ClassNotFoundException e) {
            fail(e.toString());
        }
    }

}
