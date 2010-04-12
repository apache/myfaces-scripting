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
import org.apache.myfaces.scripting.api.Configuration;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.support.ContextUtils;
import org.apache.myfaces.scripting.core.support.PathUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.java.compiler.CompilerFacade;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class TestCompilerAPI {

    private static final String JAVA_FILE_ENDING = ".java";

    private static final PathUtils _pathUtils = new PathUtils();
    private static final String PROBE1 = _pathUtils.getResource("compiler/TestProbe1.java");
    private static final String PROBE2 = _pathUtils.getResource("compiler/TestProbe2.java");
    private static final String RESOURCES = _pathUtils.getResource(".");

    private static final String CLASSFILE1_IS_COMPILED = "Class file1 is compiled into the target";
    private static final String CLASSFILE2_IS_COMPILED = "Class file2 is compiled into the target";
    private static final String TARGET_EXISTS = "target exists files are compiled into the target";
    private static final String CLASSFILE_1_IS_NOT_COMPILED = "Class file_1 is not compiled into the target";
    private static final String CLASSFILE_2_IS_NOT_COMPILED = "Class file_2 is not compiled into the target";
    private static final String CLASSFILE_3_IS_COMPILED = "Class file_3 is compiled into the target";

    File _probe1;
    File _probe2;
    File _root;

    public TestCompilerAPI() {
        String sourcePath1 = PROBE1;
        String sourcePath2 = PROBE2;
        String rootPath = RESOURCES;

        sourcePath1 = FilenameUtils.normalize(sourcePath1);
        sourcePath2 = FilenameUtils.normalize(sourcePath2);
        rootPath = FilenameUtils.normalize(rootPath);

        _probe1 = new File(sourcePath1);
        _probe2 = new File(sourcePath2);
        _root = new File(rootPath);

        WeavingContext.setConfiguration(new Configuration());
        WeavingContext.getConfiguration().addSourceDir(ScriptingConst.ENGINE_TYPE_JSF_JAVA, _root.getAbsolutePath());
    }

    @Test
    public void testDummy() {

    }

    @Test
    public void testWhiteListedCompile() {
        //WeavingContext.getConfiguration().getWhitelistedSourceDirs(ScriptingConst.ENGINE_TYPE_JSF_JAVA).clear();
        WeavingContext.getConfiguration().addWhitelistPackage("compiler.myPackage");

        try {

            /*DynamicCompiler compiler = (DynamicCompiler) new CompilerFacade(false);//new ReflectCompilerFacade();
            try {
                FileUtils.deleteDirectory(WeavingContext.getConfiguration().getCompileTarget());
            } catch (IOException e) {
                fail(e.getMessage());
            }
            WeavingContext.getConfiguration().getCompileTarget().mkdirs();

            File target = compiler.compileAllFiles(_root.getAbsolutePath(), "");
            */
            File target = ContextUtils.doJavaRecompile(_root.getAbsolutePath());

            assertTrue(TARGET_EXISTS, target != null);
            File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1.class");
            File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2.class");
            File classFile3 = new File(target.getAbsolutePath() + "/compiler/myPackage/WhiteListedProbe.class");

            assertTrue(CLASSFILE_1_IS_NOT_COMPILED, !classFile1.exists());
            assertTrue(CLASSFILE_2_IS_NOT_COMPILED, !classFile2.exists());
            assertTrue(CLASSFILE_3_IS_COMPILED, classFile3.exists());

        } catch (ClassNotFoundException e) {
            fail(e.toString());
        }

    }

    @Test
    public void testFullCompile() {
        File targetDir = null;
        try {

            DynamicCompiler compiler = (DynamicCompiler) new CompilerFacade(false);//new ReflectCompilerFacade();

            File target = compiler.compileAllFiles(_root.getAbsolutePath(), "");

            assertTrue(TARGET_EXISTS, target != null);
            File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1.class");
            File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2.class");

            assertTrue(CLASSFILE1_IS_COMPILED, classFile1.exists());
            assertTrue(CLASSFILE2_IS_COMPILED, classFile2.exists());
        } catch (ClassNotFoundException e) {
            fail(e.toString());
        }
    }

}