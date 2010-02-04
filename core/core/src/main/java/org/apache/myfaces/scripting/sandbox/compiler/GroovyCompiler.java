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
package org.apache.myfaces.scripting.sandbox.compiler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.sandbox.loader.ClassLoaderUtils;
import org.apache.myfaces.scripting.api.CompilationException;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.FileUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * <p>A compiler implementation that can be used to compile Groovy source files.</p>
 */
public class GroovyCompiler implements Compiler {

    /**
     * The logger instance for this class.
     */
    private static final Log logger = LogFactory.getLog(GroovyCompiler.class);

    // ------------------------------------------ Compiler methods

    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath  the path to the source directory
     * @param targetPath  the path to the target directory
     * @param file        the file of the class you want to compile
     * @param classLoader the class loader to use to determine the classpath
     * @return the compilation result
     */
    public CompilationResult compile(File sourcePath, File targetPath, String file, ClassLoader classLoader)
            throws CompilationException {
        return compile(sourcePath, targetPath, new File(sourcePath, file), classLoader);
    }

    public CompilationResult compile(File sourcePath, File targetPath, ClassLoader classLoader) {

        List<File> sourceFiles = FileUtils.fetchSourceFiles(sourcePath, "*.groovy");

        StringWriter compilerOutput = new StringWriter();

        CompilationUnit compilationUnit = new CompilationUnit(
                buildCompilerConfiguration(sourcePath, targetPath, classLoader));
        compilationUnit.getConfiguration().setOutput(new PrintWriter(compilerOutput));

        for (File sourceFile : sourceFiles) {
            compilationUnit.addSource(sourceFile);
        }

        CompilationResult result;

        try {
            compilationUnit.compile();

            result = new CompilationResult(compilerOutput.toString());
            WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_GROOVY, result);

        } catch (CompilationFailedException ex) {
            // Register all collected error messages from the Groovy compiler
            result = new CompilationResult(compilerOutput.toString());
            ErrorCollector collector = compilationUnit.getErrorCollector();
            for (int i = 0; i < collector.getErrorCount(); ++i) {
                result.registerError(convertMessage(collector.getError(i)));
            }
        }

        // Register all collected warnings from the Groovy compiler
        ErrorCollector collector = compilationUnit.getErrorCollector();
        for (int i = 0; i < collector.getWarningCount(); ++i) {
            result.registerWarning(convertMessage(collector.getWarning(i)));
        }

        return result;
    }

    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath  the path to the source directory
     * @param targetPath  the path to the target directory
     * @param file        the file of the class you want to compile
     * @param classLoader the class loader to use to determine the classpath
     * @return the compilation result
     */
    public CompilationResult compile(File sourcePath, File targetPath, File file, ClassLoader classLoader)
            throws CompilationException {
        StringWriter compilerOutput = new StringWriter();

        CompilationUnit compilationUnit = new CompilationUnit(
                buildCompilerConfiguration(sourcePath, targetPath, classLoader));
        compilationUnit.getConfiguration().setOutput(new PrintWriter(compilerOutput));
        compilationUnit.addSource(file);

        CompilationResult result;

        try {
            compilationUnit.compile();

            result = new CompilationResult(compilerOutput.toString());

            WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_GROOVY, result);
        } catch (CompilationFailedException ex) {
            // Register all collected error messages from the Groovy compiler
            result = new CompilationResult(compilerOutput.toString());
            ErrorCollector collector = compilationUnit.getErrorCollector();
            for (int i = 0; i < collector.getErrorCount(); ++i) {
                result.registerError(convertMessage(collector.getError(i)));
            }
        }

        // Register all collected warnings from the Groovy compiler
        ErrorCollector collector = compilationUnit.getErrorCollector();
        for (int i = 0; i < collector.getWarningCount(); ++i) {
            result.registerWarning(convertMessage(collector.getWarning(i)));
        }

        return result;
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Converts the given Groovy compiler message into a compilation message that
     * our compilation API consists of.</p>
     *
     * @param message the Groovy compiler message you want to convert
     * @return the final converted compilation message
     */
    protected CompilationResult.CompilationMessage convertMessage(Message message) {
        if (message instanceof SimpleMessage) {
            SimpleMessage simpleMessage = (SimpleMessage) message;
            return new CompilationResult.CompilationMessage(-1, simpleMessage.getMessage());
        } else if (message instanceof SyntaxErrorMessage) {
            SyntaxErrorMessage syntaxErrorMessage = (SyntaxErrorMessage) message;
            return new CompilationResult.CompilationMessage(
                    syntaxErrorMessage.getCause().getLine(), syntaxErrorMessage.getCause().getMessage());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "This compiler came across an unknown message kind ['" + message + "']. It will be ignored.");
            }

            return null;
        }
    }

    /**
     * <p>Configures the compiler by building its configuration object.</p>
     *
     * @param sourcePath  the path to the source directory
     * @param targetPath  the path to the target directory
     * @param classLoader the class loader to use to determine the classpath
     * @return the compiler configuration
     */
    protected CompilerConfiguration buildCompilerConfiguration(File sourcePath, File targetPath, ClassLoader classLoader) {
        CompilerConfiguration configuration = new CompilerConfiguration();

        // Set the destination / target directory for the compiled .class files.
        configuration.setTargetDirectory(targetPath.getAbsoluteFile());

        // Specify the classpath of the given class loader. This enables the user to write new Java
        // "scripts" that depend on classes that have already been loaded previously. Otherwise he
        // wouldn't be able to use for example classes that are available in a library.
        configuration.setClasspath(ClassLoaderUtils.buildClasspath(classLoader));

        // Enable verbose output.
        configuration.setVerbose(true);

        // Generate debugging information.
        configuration.setDebug(true);

        return configuration;
    }

}
