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

import org.apache.myfaces.extensions.scripting.AbstractGeneratorTestCase;
import org.apache.myfaces.scripting.sandbox.compiler.*;
import org.apache.myfaces.scripting.sandbox.compiler.Compiler;

import java.io.File;

/**
 * <p>Test class for
 * <code>org.apache.myfaces.scripting.sandbox.compiler.GroovyCompiler</code></p>
 */
public class GroovyCompilerTest extends AbstractGeneratorTestCase {

    // ------------------------------------------ Test methods

    /**
     * <p>Tests whether it is possible to compile a dynamically generated Groovy source file.</p>
     *
     * @throws Exception if an error occurs
     */
    public void testCompileGeneratedFile() throws Exception {
        writeFile("/src/main/groovy/org/apache/myfaces/extensions/scripting/HelloWorld.groovy", new String[]{
                "package org.apache.myfaces.extensions.scripting;   ",
                "                                                   ",
                "def class HelloWorld {                             ",
                "                                                   ",
                "   def static main(String[] args) {                ",
                "       println(\"Hello World\");                   ",
                "   }                                               ",
                "}                                                  "
        });

        org.apache.myfaces.scripting.sandbox.compiler.Compiler compiler = new GroovyCompiler();
        CompilationResult result = compiler.compile(
                new File(buildAbsolutePath("/src/main/groovy")),
                new File(buildAbsolutePath("/target/test-classes")),
                "org/apache/myfaces/extensions/scripting/HelloWorld.groovy", getCurrentClassLoader());

        assertFalse(result.hasErrors());
        assertTrue(new File(
                buildAbsolutePath("/target/test-classes/"), "org/apache/myfaces/extensions/scripting/HelloWorld.class").exists());
    }

    /**
     * <p>Tests whether compilation fails in case of an invalid Groovy source file.</p>
     *
     * @throws Exception if an error occurs
     */
    public void testCompileGeneratedFileWithError() throws Exception {
        writeFile("/src/main/groovy/org/apache/myfaces/extensions/scripting/HelloWorld.groovy", new String[]{
                "package org.apache.myfaces.extensions.scripting;   ",
                "                                                   ",
                "def class HelloWorld {                             ",
                "                                                   ",
                "   def static main(String[] args) {                ",
                "       System2.out.println(\"Hello World\");       ",
                "   }                                               ",
                "}                                                  "
        });

        Compiler compiler = new GroovyCompiler();
        CompilationResult result = compiler.compile(
                new File(buildAbsolutePath("/src/main/groovy")),
                new File(buildAbsolutePath("/target/test-classes")),
                "org/apache/myfaces/extensions/scripting/HelloWorld.groovy", getCurrentClassLoader());

        assertTrue(result.hasErrors());
        assertFalse(new File(
                buildAbsolutePath("/target/test-classes/"), "org/apache/myfaces/extensions/scripting/HelloWorld.class").exists());
    }

    /**
     * <p>Tests whether it is possible to reference dependencies in the Groovy
     * source files that have to be resolved using the supplied class loader.</p>
     *
     * @throws Exception if an error occurs
     */
    public void testCompileFileWithDependencies() throws Exception {
        writeFile("/src/main/groovy/org/apache/myfaces/extensions/scripting/DummyCompiler.groovy", new String[]{
                "package org.apache.myfaces.extensions.scripting;                                   ",
                "                                                                                   ",
                "import java.io.File;                                                               ",
                "import org.apache.myfaces.scripting.sandbox.compiler.Compiler;                  ",
                "import org.apache.myfaces.scripting.sandbox.compiler.CompilationResult;         ",
                "                                                                                   ",
                "def class DummyCompiler implements Compiler {                                      ",
                "                                                                                   ",
                "   def CompilationResult compile(File s, File t, String f, ClassLoader c) {        ",
                "       return null;                                                                ",
                "   }                                                                               ",
                "                                                                                   ",
                "   def CompilationResult compile(File s, File t, File f, ClassLoader c) {          ",
                "       return null;                                                                ",
                "   }                                                                               ",
                "}                                                                                  "
        });

        Compiler compiler = new GroovyCompiler();
        CompilationResult result = compiler.compile(
                new File(buildAbsolutePath("/src/main/groovy")),
                new File(buildAbsolutePath("/target/test-classes")),
                "org/apache/myfaces/extensions/scripting/DummyCompiler.groovy", getCurrentClassLoader());

        assertFalse(result.hasErrors());
        assertTrue(new File(
                buildAbsolutePath("/target/test-classes/"), "org/apache/myfaces/extensions/scripting/DummyCompiler.class").exists());
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Returns the class loader that has loaded this class.</p>
     *
     * @return the class loader that has loaded this class
     */
    protected ClassLoader getCurrentClassLoader() {
        return getClass().getClassLoader();
    }

}
