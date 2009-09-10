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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.core.util.Null;
import org.apache.myfaces.scripting.core.util.Cast;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.ReflectUtil;


import java.io.File;
import java.io.Writer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Collection;
import java.nio.charset.Charset;

import org.apache.myfaces.scripting.api.DynamicCompiler;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          The Compiler facade based on the reflection api
 *          to allow jdk5 compilations
 */

public class ReflectCompilerFacade  implements DynamicCompiler {
    //TODO add optional ecj dependencies here
    Object compiler = null;
    Object diagnosticCollector = null;
    Object fileManager = null;
    private static final String FILE_SEPARATOR = File.separator;

    public ReflectCompilerFacade() {
        super();

        //TODO move this all into the introspection domain
        //so that we can shift to jdk5
        Class toolProviderClass = ClassUtils.forName("javax.tools.ToolProvider");
        compiler = ReflectUtil.executeStaticFunction(toolProviderClass, "getSystemJavaCompiler");
        diagnosticCollector = ReflectUtil.instantiate("javax.tools.DiagnosticCollector");

        fileManager = ReflectUtil.instantiate("org.apache.myfaces.scripting.loaders.java.jsr199.ContainerFileManager",
                                  new Cast(ClassUtils.forName("javax.tools.StandardJavaFileManager"), ReflectUtil.executeFunction(compiler, "getStandardFileManager", new Cast(ClassUtils.forName("javax.tools.DiagnosticListener"),diagnosticCollector), new Null(Locale.class), new Null(Charset.class))));

    }


    public Class compileFile(String sourceRoot, String classPath, String filePath) throws ClassNotFoundException {
        Object fileObjects = ReflectUtil.executeFunction(fileManager, "getJavaFileObjectsSingle",  sourceRoot + FILE_SEPARATOR + filePath)  ;

        //TODO add the core jar from our lib dir
        //the javaCompiler otherwise cannot find the file
        String[] options = new String[]{"-cp",
                                        (String) ReflectUtil.executeFunction(fileManager, "getClassPath"), "-d", (String) ReflectUtil.executeFunction(ReflectUtil.executeFunction(fileManager, "getTempDir"), "getAbsolutePath"), "-sourcepath", sourceRoot, "-g"};

        ReflectUtil.executeMethod(ReflectUtil.executeFunction(compiler, "getTask", new Null(Writer.class), new Cast(ClassUtils.forName("javax.tools.JavaFileManager"), fileManager),new Cast(ClassUtils.forName("javax.tools.DiagnosticListener"), diagnosticCollector),new Cast(java.lang.Iterable.class, Arrays.asList(options)), new Null(Iterable.class), new Cast(java.lang.Iterable.class,fileObjects)), "call");
        //TODO collect the diagnostics and if an error was issued dump it on the log
        //and throw an unmanaged exeption which routes later on into myfaces
        Collection diagnostics = (Collection) ReflectUtil.executeFunction(diagnosticCollector, "getDiagnostics");
        Integer size = diagnostics.size();
        if (size > 0) {
            Log log = LogFactory.getLog(this.getClass());
            StringBuilder errors = new StringBuilder();
            for (Object diagnostic : diagnostics) {
                String error = "Error on line" +
                               ReflectUtil.executeFunction(diagnostic, "getMessage", Locale.getDefault()) + "------" +
                               ReflectUtil.executeFunction(diagnostic, "getLineNumber") + " File:" +
                               ReflectUtil.executeFunction(diagnostic, "getSource").toString();
                log.error(error);
                errors.append(error);

            }
            throw new ClassNotFoundException("Compile error of java file:" + errors.toString());
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        if (!(oldClassLoader instanceof RecompiledClassLoader)) {
            try {
                RecompiledClassLoader classLoader = (RecompiledClassLoader) ReflectUtil.executeFunction(fileManager, "getClassLoader");
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
