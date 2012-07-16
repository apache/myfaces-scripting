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

package org.apache.myfaces.extensions.scripting.core.engine.compiler;

import org.apache.commons.io.FilenameUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.myfaces.extensions.scripting.core.api.Configuration;
import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import org.apache.myfaces.extensions.scripting.core.engine.FactoryEngines;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class JavaCompilerTest
{
    private static final String PROBE1 = "../../src/test/resources/compiler/TestProbe1.java";
    private static final String PROBE2 = "../../src/test/resources/compiler/TestProbe2.java";
    private static final String RESOURCES = "../../src/test/resources/";

    File probe1;
    File probe2;
    File root;

    private static final String RESULT_HAS_NO_ERRORS = "result has no errors";
    private static final String TARGET_DIR_EXISTS = "targetDir exists files are compiled into the targetDir";
    private static final String CLASSFILE1_IS_COMPILED = "Classfile1 is compiled into the targetDir";
    private static final String CLASSFILE1_IS_COMPILED1 = "Classfile1 is compiled into the target";
    private static final String CLASSFILE2_IS_COMPILED = "Classfile2 is compiled into the target";

    public JavaCompilerTest()
    {
        try
        {
            FactoryEngines.getInstance().init();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //we use a location relative to our current root one to reach the sources
        //because the test also has to be performed outside of maven
        //and the ide cannot cope with resource paths for now
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String currentPath = null;
        try
        {
            currentPath = URLDecoder.decode(loader.getResource("./").getPath(), Charset.defaultCharset().toString());
        }
        catch (UnsupportedEncodingException e)
        {
            fail(e.getMessage());
        }
        String sourcePath1 = currentPath + PROBE1;
        String sourcePath2 = currentPath + PROBE2;
        String rootPath = currentPath + RESOURCES;

        sourcePath1 = FilenameUtils.normalize(sourcePath1);
        sourcePath2 = FilenameUtils.normalize(sourcePath2);
        rootPath = FilenameUtils.normalize(rootPath);

        probe1 = new File(sourcePath1);
        probe2 = new File(sourcePath2);
        root = new File(rootPath);

        WeavingContext.getInstance().setConfiguration(new Configuration());
        WeavingContext.getInstance().getConfiguration().addSourceDir(ScriptingConst.ENGINE_TYPE_JSF_JAVA,
                root.getAbsolutePath());

    }

    @Test
    public void testInMemoryCompile() {
        String sourceCode =
                  "class DynamicCompilationHelloWorld{" +
                          "public static void main (String args[]){" +
                          "System.out.println (\"Hello, dynamic compilation world!\");" +
                          "}" +
                          "}";
          /*Creating dynamic java source code file object*/
          SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject("DynamicCompilationHelloWorld", sourceCode);
          JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject};

          /* Prepare a list of compilation units (java source code file objects) to input to compilation task*/
          Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);

          /*Prepare any compilation options to be used during compilation*/
          //In this example, we are asking the compiler to place the output files under bin folder.

          String[] compileOptions = new String[]{"-d", FileUtils.getTempDir().getAbsolutePath()};
          Iterable<String> compilationOptions = Arrays.asList(compileOptions);

          /*Instantiating the java compiler*/
          JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

          /**
           * Retrieving the standard file manager from compiler object, which is used to provide
           * basic building block for customizing how a compiler reads and writes to files.
           *
           * The same file manager can be reopened for another compiler task.
           * Thus we reduce the overhead of scanning through file system and jar files each time
           */
          StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
          /*Create a diagnostic controller, which holds the compilation problems*/
          DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
          /*Create a compilation task from compiler by passing in the required input objects prepared above*/
          JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptions, null, compilationUnits);

          //Perform the compilation by calling the call method on compilerTask object.
          boolean status = compilerTask.call();

          if (!status)
          {//If compilation error occurs
              /*Iterate through each compilation problem and print it*/
              for (Diagnostic diagnostic : diagnostics.getDiagnostics())
              {
                  System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
              }
          }
    }

    @Test
    public void testFullCompileWhitelist()
    {
        JSR199Compiler compiler = new JSR199Compiler();

        File targetDir = null;

        File target = WeavingContext.getInstance().getConfiguration().getCompileTarget();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        target.mkdirs();
        target.deleteOnExit();

        WeavingContext.getInstance().getConfiguration().addWhitelistPackage("compiler.myPackage");

        CompilationResult result = null;

        result = compiler.compile(root, target, loader);

        assertTrue(RESULT_HAS_NO_ERRORS, !result.hasErrors());

        assertTrue("target exists files are compiled into the target", target != null);
        File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1.class");
        File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2.class");
        File classFile3 = new File(target.getAbsolutePath() + "/compiler/myPackage/WhiteListedProbe.class");

        assertTrue(CLASSFILE1_IS_COMPILED1, !classFile1.exists());
        assertTrue(CLASSFILE2_IS_COMPILED, !classFile2.exists());
        assertTrue(CLASSFILE2_IS_COMPILED, classFile3.exists());
        classFile3.delete();
        WeavingContext.getInstance().getConfiguration().getCompileTarget().delete();
        WeavingContext.getInstance().getConfiguration().getCompileTarget().mkdirs();
    }

    @Test
    public void testFullCompile()
    {
        JSR199Compiler compiler = new JSR199Compiler();
        File targetDir = null;

        File target = FileUtils.getTempDir();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        target.mkdirs();
        target.deleteOnExit();

        CompilationResult result = null;
        result = compiler.compile(root, target, loader);

        assertTrue(RESULT_HAS_NO_ERRORS, !result.hasErrors());
        System.out.println(result.getCompilerOutput());
        assertTrue(TARGET_DIR_EXISTS, target != null);
        File classFile1 = new File(target.getAbsolutePath() + "/compiler/TestProbe1.class");
        File classFile2 = new File(target.getAbsolutePath() + "/compiler/TestProbe2.class");

        assertTrue(CLASSFILE1_IS_COMPILED1, classFile1.exists());
        assertTrue(CLASSFILE2_IS_COMPILED, classFile2.exists());
        classFile1.delete();
        classFile2.delete();
        WeavingContext.getInstance().getConfiguration().getCompileTarget().delete();
        WeavingContext.getInstance().getConfiguration().getCompileTarget().mkdirs();
        //testFullCompileWhitelist();
    }

}

