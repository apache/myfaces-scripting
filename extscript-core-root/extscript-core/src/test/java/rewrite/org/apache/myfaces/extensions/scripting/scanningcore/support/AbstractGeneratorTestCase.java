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
package org.apache.myfaces.extensions.scripting.scanningcore.support;

import junit.framework.TestCase;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationException;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;
import org.apache.myfaces.extensions.scripting.core.engine.compiler.JSR199Compiler;
import org.apache.myfaces.extensions.scripting.core.engine.api.Compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>Base class for test cases that generate Java source files.</p>
 */
public abstract class AbstractGeneratorTestCase extends TestCase {

    /**
     * The temporary test directory where test cases store generated Java source files, etc.
     */
    private File testDirectory;

    // ------------------------------------------ Test lifecycle methods

    /**
     * <p>Creates a temporary directory that can be used to store
     * generated source files and compiled class files within it.</p>
     */
    @Override
    public void setUp() throws Exception {
        // Create the test directory within the directory that the class file of this test case is located in
        testDirectory =
                new File(getClass().getResource(".").toURI().getPath(), "test");
        if (!testDirectory.mkdirs() && !testDirectory.exists()) {
            throw new IllegalStateException(
                    "Couldn't setup the test case for the test case '" + getClass().getName()
                            + "'. It wasn't possible to create a temporary test folder.");
        }
    }

    /**
     * <p>Deletes the temporary directory including all subdirectories
     * and files within it.</p>
     */
    @Override
    protected void tearDown() throws Exception {
        deleteDirectory(testDirectory);

        if (!testDirectory.delete()) {
            System.err.println("Couldn't delete the temporary test directory '" + testDirectory.getAbsolutePath() + "'.");
        }
    }

    // ------------------------------------------ Protected methods

    /**
     * <p>Writes the given file content to the specified file. Use this method in order to
     * persist dynamically generated Java code. Note that this method assumes that the given
     * file name is a relative path to the test directory.</p>
     *
     * @param fileName    the Java source file that you want to save
     * @param fileContent the content that you want to save, i.e. the Java code
     * @throws java.io.IOException if an I/O-error occurs
     */
    protected void writeFile(String fileName, String[] fileContent) throws IOException {
        writeFile(new File(testDirectory, fileName), fileContent);
    }

    /**
     * <p>Writes the given file content to the specified file. Use this method in order to
     * persist dynamically generated Java code.</p>
     *
     * @param file        the Java source file that you want to save
     * @param fileContent the content that you want to save, i.e. the Java code
     * @throws java.io.IOException if an I/O-error occurs
     */
    protected void writeFile(File file, String[] fileContent) throws IOException {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs() && !!file.createNewFile()) {
            throw new IllegalStateException("Couldn't create the file '" + file.getAbsolutePath() + "'.");
        }

        PrintWriter writer = new PrintWriter(new FileOutputStream(file));
        for (String line : fileContent) {
            writer.println(line);
        }

        writer.flush();
        writer.close();

        // Wait a little bit so that the system updates the timestamps
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /*protected CompilationResult compileFile(String sourcePath, String targetPath, String fileName,
                                             String[] fileContent)
            throws IOException, CompilationException
    {
        return compileFile(new JSR199Compiler(),
                new File(buildAbsolutePath(sourcePath)), new File(buildAbsolutePath(targetPath)), fileName,
            fileContent);
    }

    protected CompilationResult compileFile(String sourcePath, String targetPath, String fileName, String[] fileContent, ClassLoader classLoader)
            throws IOException, CompilationException {
            return compileFile(new JSR199Compiler(),
              new File(buildAbsolutePath(sourcePath)), new File(buildAbsolutePath(targetPath)), fileName,
                fileContent, classLoader);
    }

    protected CompilationResult compileFile(Compiler compiler, String sourcePath, String targetPath,
                                             String fileName, String[] fileContent)
            throws IOException, CompilationException {
            return compileFile(compiler,
                new File(buildAbsolutePath(sourcePath)), new File(buildAbsolutePath(targetPath)), fileName,
                fileContent);
    }

    protected CompilationResult compileFile(Compiler compiler, File sourcePath, File targetPath, String fileName, String[] fileContent)
            throws IOException, CompilationException {
        return compileFile(compiler, sourcePath, targetPath, fileName, fileContent, getClass().getClassLoader());
    }

    protected CompilationResult compileFile(Compiler compiler, File sourcePath, File targetPath, String fileName, String[] fileContent, ClassLoader classLoader)
            throws IOException, CompilationException {
        writeFile(new File(sourcePath, fileName), fileContent);

        CompilationResult result = compiler.compile(sourcePath, targetPath, fileName, classLoader);
        assertFalse("Compilation errors: " + result.getErrors().toString(), result.hasErrors());

        return result;
    }  */

    /**
     * <p>Concatenates the given relative path and the path of the test directory. In doing so
     * an absolute path will be created that you can use to access the according file.</p>
     *
     * @param relativePath the relative path of the file that you want to access
     * @return the absolute path of the file that you want to access
     */
    protected String buildAbsolutePath(String relativePath) {
        return buildAbsoluteFile(relativePath).getAbsolutePath();
    }

    /**
     * <p>Concatenates the given relative path and the path of the test directory. In doing so
     * an absolute file will be created that you can use to access the according file.</p>
     *
     * @param relativePath the relative path of the file that you want to access
     * @return the absolute File object of the file that you want to access
     */
    protected File buildAbsoluteFile(String relativePath) {
        File file = new File(testDirectory, relativePath);
        if (!file.exists() && !file.mkdirs()) {
            throw new IllegalStateException("Couldn't create the directory '" + file.getAbsolutePath() + "'.");
        }

        return file;
    }

    // ------------------------------------------ Private utility methods

    /**
     * <p>Deletes all subdirectories and files within the given directory.</p>
     *
     * @param directory the directory you want to delete
     */
    public static void deleteDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            }

            if (!file.delete()) {
                System.err.println("Couldn't delete the file or directory '" + file.getAbsolutePath() + "'.");
            }
        }
    }

}
