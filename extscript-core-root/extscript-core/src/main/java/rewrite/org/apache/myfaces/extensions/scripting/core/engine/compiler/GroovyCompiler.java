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
package rewrite.org.apache.myfaces.extensions.scripting.core.engine.compiler;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import rewrite.org.apache.myfaces.extensions.scripting.core.api.Configuration;
import rewrite.org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import rewrite.org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.ClassLoaderUtils;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>A compiler implementation that can be used to compile Groovy source files.</p>
 */
public class GroovyCompiler implements rewrite.org.apache.myfaces.extensions.scripting.core.engine.api.Compiler
{

    /**
     * The logger instance for this class.
     */
    private static final Logger _logger = Logger.getLogger(GroovyCompiler.class.getName());

    CompilationResult result = null;
    // ------------------------------------------ Compiler methods


    public CompilationResult compile(File sourcePath, File targetPath, ClassLoader classLoader) {
        WeavingContext context = WeavingContext.getInstance();
        Configuration configuration = context.getConfiguration();
        
        
        
        List<File> sourceFiles = FileUtils.fetchSourceFiles(configuration.getWhitelistedSourceDirs(ScriptingConst
                .ENGINE_TYPE_JSF_GROOVY), "*.groovy");

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
            //context.setCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_GROOVY, result);
            //this.result = result;
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
            if (_logger.isLoggable(Level.FINE)) {
                _logger.log(Level.FINE,
                        "This compiler came across an unknown message kind ['{0}']. It will be ignored.", message);
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
