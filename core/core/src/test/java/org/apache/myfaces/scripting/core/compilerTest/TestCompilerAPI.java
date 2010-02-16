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
package org.apache.myfaces.scripting.core.compilerTest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.myfaces.scripting.api.Configuration;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ReflectUtil;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.java.compiler.CompilerFacade;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class TestCompilerAPI {

    private static final String JAVA_FILE_ENDING = ".java";

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

        WeavingContext.setConfiguration(new Configuration());
        WeavingContext.getConfiguration().addSourceDir(ScriptingConst.ENGINE_TYPE_JAVA, root.getAbsolutePath());
    }

    @Test
    public void testDummy() {

    }

    @Test
    public void testWhiteListedCompile() {
        //WeavingContext.getConfiguration().getWhitelistedSourceDirs(ScriptingConst.ENGINE_TYPE_JAVA).clear();
        WeavingContext.getConfiguration().addWhitelistPackage("compiler.myPackage");

        try {

            DynamicCompiler compiler = (DynamicCompiler) new CompilerFacade(false);//new ReflectCompilerFacade();
            try {
                FileUtils.deleteDirectory(WeavingContext.getConfiguration().getCompileTarget());
            } catch (IOException e) {
                fail(e.getMessage());
            }
            WeavingContext.getConfiguration().getCompileTarget().mkdirs();

            File target = compiler.compileAllFiles(root.getAbsolutePath(), "");

            assertTrue("target exists files are compiled into the target", target != null);
            File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1.class");
            File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2.class");
            File classFile3 = new File(target.getAbsolutePath() + "/compiler/myPackage/WhiteListedProbe.class");
   
            assertTrue("Classfile_1 is not compiled into the target",    !classFile1.exists());
            assertTrue("Classfile_2 is not compiled into the target",    !classFile2.exists());
            assertTrue("Classfile_3 is compiled into the target",        classFile3.exists());

        } catch (ClassNotFoundException e) {
            fail(e.toString());
        }

    }

    @Test
    public void testFullCompile() {
        File targetDir = null;
        try {

            DynamicCompiler compiler = (DynamicCompiler) new CompilerFacade(false);//new ReflectCompilerFacade();

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