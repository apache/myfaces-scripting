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

import org.apache.myfaces.extensions.scripting.core.api.Configuration;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassLoaderUtils;
import org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationMessage;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.ENGINE_TYPE_JSF_JAVA;
import static org.apache.myfaces.extensions.scripting.core.engine.api.CompilerConst.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p></p>
 *          a JSR 199 based compiler which implements
 *          our simplified compiler interface
 */
public class JSR199Compiler implements org.apache.myfaces.extensions.scripting.core.engine.api.Compiler
{

    javax.tools.JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = null;


    public JSR199Compiler()
    {
        super();
    }

    /**
     * compile all files starting from a given root
     * <p></p>
     * note, the java compiler interface does not allow per se
     * wildcards due to its file object indirection
     * we deal with that problem by determine all files manually and then
     * push the list into the jsr compiler interface
     *
     *
     * @param sourceRoot the root for all java sources to be compiled
     * @param loader     the classpath holder for the compilation
     * @return the collected compilation results as bundle
     */
    public CompilationResult compile(File sourceRoot, File destination, ClassLoader loader)   {
            WeavingContext context = WeavingContext.getInstance();
            Configuration configuration = context.getConfiguration();
            destination.mkdirs();
            fileManager =  javaCompiler.getStandardFileManager(new
                            DiagnosticCollector<JavaFileObject>(), Locale.getDefault(), null);

            DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();

            getLog().info("[EXT-SCRIPTING] Doing a full recompile");
            
            List<File> sourceFiles = FileUtils.fetchSourceFiles(configuration.getWhitelistedSourceDirs
                    (ENGINE_TYPE_JSF_JAVA), JAVA_WILDCARD);
            
            HashSet<String> alreadyProcessed = new HashSet<String>();
            Iterator<File> sourceIt = sourceFiles.iterator();
            while(sourceIt.hasNext()) {
                File currentProcessed = sourceIt.next();
                if(alreadyProcessed.contains(currentProcessed.getAbsolutePath())) {
                    sourceIt.remove();
                } else {
                    alreadyProcessed.add(currentProcessed.getAbsolutePath());
                }

            }
        
        
            for (File sourceFile : sourceFiles)
            {
                if (!sourceFile.exists())
                {
                    getLog().log(Level.WARNING, "[EXT-SCRIPTING] Source file with path {0} does not exist it might cause an error in the compilation process", sourceFile.getAbsolutePath());
                }
            }
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(sourceFiles.toArray(new File[sourceFiles.size()]));
            String[] options = new String[]{JC_CLASSPATH, /*fileManager.getClassPath()*/
                                            ClassLoaderUtils.buildClasspath(ClassLoaderUtils.getDefaultClassLoader())
                    , JC_TARGET_PATH,
                                            destination.getAbsolutePath(), JC_SOURCEPATH,
            sourceRoot.getAbsolutePath(), JC_DEBUG};

            javaCompiler.getTask(null, fileManager, diagnosticCollector, Arrays.asList(options), null, fileObjects).call();


            CompilationResult result =  handleDiagnostics(diagnosticCollector);


            return result;

    }

    /**
     * internal diagnostics handler
     * which just logs the errors
     *
     * @param diagnosticCollector the compilation results, the jsr 199 uses a DiagnosticsCollector object
     *                            to keep the errors and warnings of the compiler
     * @throws ClassNotFoundException in case of an error (this is enforced by the compiler interface
     *                                and probably will be overhauled in the long run)
     */
    private CompilationResult handleDiagnostics(DiagnosticCollector<JavaFileObject> diagnosticCollector)
    {
        if (diagnosticCollector.getDiagnostics().size() > 0)
        {
            Logger log = Logger.getLogger(this.getClass().getName());
            StringBuilder errors = new StringBuilder();
            CompilationResult result = new CompilationResult("");
            boolean hasError = false;
            for (Diagnostic diagnostic : diagnosticCollector.getDiagnostics())
            {
                String error = createErrorMessage(diagnostic);
                log.log(Level.WARNING, "[EXT-SCRIPTING] Compiler: {0}", error);

                if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR))
                {
                    hasError = true;
                    result.getErrors().add(new CompilationMessage(diagnostic.getLineNumber(), diagnostic.getMessage(Locale.getDefault())));
                } else
                {
                    result.getWarnings().add(new CompilationMessage(diagnostic.getLineNumber(), diagnostic.getMessage(Locale.getDefault())));
                }
                errors.append(error);

            }
            return result;
        } else
        {
            //WeavingContext.setCompilationResult(ENGINE_TYPE_JSF_JAVA, new CompilationResult(""));
            return new CompilationResult("");
        }
    }

    /**
     * interruption of the compile flow should only
     * happen if an error has occurred otherwise we will proceed
     * as expected
     *
     * @param errors   the errors messages found
     * @param hasError marker if an error was found or not
     * @throws ClassNotFoundException in case of a compile error
     */
    private void assertErrorFound(StringBuilder errors, boolean hasError) throws ClassNotFoundException
    {
        if (hasError)
        {
            throw new ClassNotFoundException("Compile error of java file:" + errors.toString());
        }
    }

    /**
     * creates a standardized error message
     *
     * @param diagnostic the diagnostic of the compiler containing the error data
     * @return a formatted string with the standardized error message which then later
     *         can be processed by the user
     */
    private String createErrorMessage(Diagnostic diagnostic)
    {
        StringBuilder retVal = new StringBuilder(256);
        if (diagnostic == null)
        {
            return retVal.toString();
        }
        if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR))
        {
            retVal.append(STD_ERROR_HEAD);
        } else if (diagnostic.getKind().equals(Diagnostic.Kind.NOTE))
        {
            retVal.append(STD_NOTE_HEAD);
        } else if (diagnostic.getKind().equals(Diagnostic.Kind.WARNING))
        {
            retVal.append(STD_WARN_HEAD);
        } else if (diagnostic.getKind().equals(Diagnostic.Kind.MANDATORY_WARNING))
        {
            retVal.append(STD_MANDATORY_WARN_HEAD);
        } else if (diagnostic.getKind().equals(Diagnostic.Kind.OTHER))
        {
            retVal.append(STD_OTHER_HEAD);
        }
        String message = diagnostic.getMessage(Locale.getDefault());
        message = (message == null) ? "" : message;
        retVal.append(message);
        retVal.append(diagnostic.getLineNumber());

        retVal.append("\n\n");

        String source = "No additional source info";

        if (diagnostic.getSource() != null)
        {
            source = diagnostic.getSource().toString();
        }
        retVal.append(source);

        return retVal.toString();
    }

    private Logger getLog()
    {
        return Logger.getLogger(this.getClass().getName());
    }

}