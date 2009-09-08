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
package org.apache.myfaces.scripting.loaders.java.jsr199;

import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.tools.*;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

/**
 * A compiler facade encapsulating the JSR 199
 * so that we can switch the implementations
 * of connecting to javac on the fly
 *
 * @author Werner Punz (latest modification by $Author: werpu $)
 * @version $Revision: 812255 $ $Date: 2009-09-07 20:51:39 +0200 (Mo, 07 Sep 2009) $
 */
public class CompilerFacade implements DynamicCompiler {
    //TODO add optional ecj dependencies here
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector();
    ContainerFileManager fileManager = null;
    private static final String FILE_SEPARATOR = File.separator;

    public CompilerFacade() {
        super();

        //TODO move this all into the introspection domain
        //so that we can shift to jdk5
        fileManager = new ContainerFileManager(compiler.getStandardFileManager(diagnosticCollector, null, null));

    }


    public Class compileFile(String sourceRoot, String classPath, String filePath) throws ClassNotFoundException {
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(sourceRoot + FILE_SEPARATOR + filePath);

        //TODO add the core jar from our lib dir
        //the compiler otherwise cannot find the file
        String[] options = new String[]{"-cp", fileManager.getClassPath(), "-d", fileManager.getTempDir().getAbsolutePath(), "-sourcepath", sourceRoot, "-g"};
        compiler.getTask(null, fileManager, diagnosticCollector, Arrays.asList(options), null, fileObjects).call();
        //TODO collect the diagnostics and if an error was issued dump it on the log
        //and throw an unmanaged exeption which routes later on into myfaces
        if (diagnosticCollector.getDiagnostics().size() > 0) {
            Log log = LogFactory.getLog(this.getClass());
            StringBuilder errors = new StringBuilder();
            for (Diagnostic diagnostic : diagnosticCollector.getDiagnostics()) {
                String error = "Error on line" +
                               diagnostic.getMessage(Locale.getDefault()) + "------" +
                               diagnostic.getLineNumber() + " File:" +
                               diagnostic.getSource().toString();
                log.error(error);
                errors.append(error);

            }
            throw new ClassNotFoundException("Compile error of java file:" + errors.toString());
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        if (!(oldClassLoader instanceof RecompiledClassLoader)) {
            try {
                RecompiledClassLoader classLoader = (RecompiledClassLoader) fileManager.getClassLoader(null);
                Thread.currentThread().setContextClassLoader(classLoader);
                String classFile = filePath.replaceAll("\\\\", ".").replaceAll("\\/", ".");
                classFile = classFile.substring(0, classFile.lastIndexOf("."));

                return classLoader.loadClass(classFile);
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
        return null;
    }
}