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
package org.apache.myfaces.extensions.scripting.core.compilerTest;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.extensions.scripting.api.CompilationException;
import org.apache.myfaces.extensions.scripting.api.CompilationResult;
import org.apache.myfaces.extensions.scripting.api.Configuration;
import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;

import org.apache.myfaces.extensions.scripting.core.util.FileUtils;
import org.apache.myfaces.extensions.scripting.loaders.groovy.compiler.GroovyCompiler;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Another testcase for the groovy compiler which
 * picks up the probes given in src/main/resources
 * <p/>
 * the advantage over our old groovy compiler testcase simply
 * is that the probes are separate files which already exist
 * the pattern already works on java files so it ought to work
 * on groovy files as well
 */

public class GroovyCompilerTest {

    private static final String PROBE1 = "../../src/test/resources/compiler/TestProbe1Groovy.groovy";
    private static final String PROBE2 = "../../src/test/resources/compiler/TestProbe2Groovy.groovy";
    private static final String RESOURCES = "../../src/test/resources/";

    File probe1;
    File probe2;
    File root;

    GroovyCompiler compiler = new GroovyCompiler();
    private static final String RESULT_HAS_NO_ERRORS = "result has no errors";
    private static final String TARGET_DIR_EXISTS = "targetDir exists files are compiled into the targetDir";
    private static final String CLASSFILE1_IS_COMPILED = "Classfile1 is compiled into the targetDir";
    private static final String CLASSFILE1_IS_COMPILED1 = "Classfile1 is compiled into the target";
    private static final String CLASSFILE2_IS_COMPILED = "Classfile2 is compiled into the target";

    public GroovyCompilerTest() {
        //we use a location relative to our current root one to reach the sources
        //because the test also has to be performed outside of maven
        //and the ide cannot cope with resource paths for now
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

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
        WeavingContext.getConfiguration().addSourceDir(ScriptingConst.ENGINE_TYPE_JSF_GROOVY, root.getAbsolutePath());
    }

    @Test
    public void testCompileSingleFile() {

        try {
            File targetDir = FileUtils.getTempDir();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            targetDir.mkdirs();
            targetDir.deleteOnExit();
            File sourceFile = new File(root.getAbsolutePath() + File.separator + "compiler" + File.separator + "TestProbe1Groovy.groovy");

            CompilationResult result = null;

            result = compiler.compile(root, targetDir, sourceFile, loader);

            assertTrue(RESULT_HAS_NO_ERRORS, !result.hasErrors());

            assertTrue(TARGET_DIR_EXISTS, targetDir != null);
            File classFile1 = new File(targetDir.getAbsolutePath() + "/compiler/TestProbe1Groovy.class");

            assertTrue(CLASSFILE1_IS_COMPILED, classFile1.exists());

        } catch (CompilationException e) {
            fail(e.toString());
        }
    }


    @Test
    public void testFullCompileWhitelist() {
        File targetDir = null;

        File target = FileUtils.getTempDir();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        target.mkdirs();
        target.deleteOnExit();

        WeavingContext.getConfiguration().addWhitelistPackage("compiler.myPackage");

        CompilationResult result = compiler.compile(root, target, loader);

        assertTrue(RESULT_HAS_NO_ERRORS, !result.hasErrors());

        assertTrue("target exists files are compiled into the target", target != null);
        File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1Groovy.class");
        File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2Groovy.class");
        File classFile3 = new File(target.getAbsolutePath() + "/compiler/myPackage/WhiteListedProbeGroovy.class");

        assertTrue(CLASSFILE1_IS_COMPILED1, !classFile1.exists());
        assertTrue(CLASSFILE2_IS_COMPILED, !classFile2.exists());
        assertTrue(CLASSFILE2_IS_COMPILED, classFile3.exists());

    }

    @Test
    public void testFullCompile() {
        File targetDir = null;

        File target = FileUtils.getTempDir();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        target.mkdirs();
        target.deleteOnExit();

        CompilationResult result = compiler.compile(root, target, loader);

        assertTrue(RESULT_HAS_NO_ERRORS, !result.hasErrors());

        assertTrue(TARGET_DIR_EXISTS, target != null);
        File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1Groovy.class");
        File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2Groovy.class");

        assertTrue(CLASSFILE1_IS_COMPILED1, classFile1.exists());
        assertTrue(CLASSFILE2_IS_COMPILED, classFile2.exists());

    }

}
