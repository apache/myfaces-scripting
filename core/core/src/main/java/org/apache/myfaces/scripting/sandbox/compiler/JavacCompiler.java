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

import org.apache.myfaces.scripting.api.CompilationException;
import org.apache.myfaces.scripting.api.CompilationResult;
import org.apache.myfaces.scripting.core.util.ClassLoaderUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>A compiler implementation that utilizes some internal classes that enable you to
 * compile Java source code using the javac compiler being provided by your JDK. However,
 * note that this only works if you're using a Sun JDK up to the version 1.5 (as of Java 6
 * you should use the JSR-199 API).</p>
 * <p/>
 * <p>This class loads some internal classes from $JAVA_HOME$/lib/tools.jar so be sure to
 * either include this JAR file in your classpath at startup or set the JAVA_HOME property
 * accordingly so that it points to a valid JDK home directory (it doesn't work if you're
 * just using a JRE!)</p>
 */
public class JavacCompiler implements Compiler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(JavacCompiler.class.getName());

    /**
     * The class name of the javac compiler. Note that this class
     * is only available if you're using a Sun JDK.
     */
    private static final String JAVAC_MAIN = "com.sun.tools.javac.Main";

    /**
     * The class reference to the internal Javac compiler.
     */
    private Class compilerClass;

    // ------------------------------------------ Constructors

    /**
     * <p>Creates a new Javac compiler by searching for the required JAR file '$JAVA_HOME$/lib/tools.jar'
     * automatically. Note that the user has to specify the JAVA_HOME property in this case.</p>
     */
    public JavacCompiler() {
        this(null);
    }

    /**
     * <p>Creates a new Javac compiler by searching for internal classes in the given JAR file.</p>
     *
     * @param toolsJar the location of the JAR file '$JAVA_HOME$/lib/tools.jar' or <code>null</code>
     *                 if you want it to be searched for automatically
     */
    public JavacCompiler(URL toolsJar) {
        ClassLoader classLoader;

        try {
            classLoader = createJavacAwareClassLoader(toolsJar);
        }
        catch (MalformedURLException ex) {
            throw new IllegalStateException("An error occurred while trying to load the Javac compiler class.", ex);
        }

        try {
            this.compilerClass = classLoader.loadClass(JAVAC_MAIN);
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("The Javac compiler class '" + JAVAC_MAIN + "' couldn't be found even though" +
                    "the required JAR file '$JAVA_HOME$/lib/tools.jar' has been put on the classpath. Are you sure that " +
                    "you're using a valid Sun JDK?");
        }
    }

    // ------------------------------------------ Compiler methods

    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param file       the relative file name of the class you want to compile
     * @return the compilation result, i.e. as of now only the compiler output
     */
    public CompilationResult compile(File sourcePath, File targetPath, String file, ClassLoader classLoader)
            throws CompilationException {
        return compile(sourcePath, targetPath, new File(sourcePath, file), classLoader);
    }

    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param file       the file of the class you want to compile
     * @return the compilation result, i.e. as of now only the compiler output
     */
    public CompilationResult compile(File sourcePath, File targetPath, File file, ClassLoader classLoader)
            throws CompilationException {
        // The destination directory must already exist as javac will not create the destination directory.
        if (!targetPath.exists()) {
            if (!targetPath.mkdirs()) {
                throw new IllegalStateException("It wasn't possible to create the target " +
                        "directory for the compiler ['" + targetPath.getAbsolutePath() + "'].");
            }

            // If we've created the destination directory, we'll delete it as well once the application exits
            targetPath.deleteOnExit();
        }

        try {
            StringWriter compilerOutput = new StringWriter();

            // Invoke the Javac compiler
            Method compile = compilerClass.getMethod("compile", new Class[]{String[].class, PrintWriter.class});
            Integer returnCode = (Integer) compile.invoke(null,
                    new Object[]{buildCompilerArguments(sourcePath, targetPath, file, classLoader),
                                 new PrintWriter(compilerOutput)});

            CompilationResult result = new CompilationResult(compilerOutput.toString());
            if (returnCode == null || returnCode.intValue() != 0) {
                result.registerError(new CompilationResult.CompilationMessage(-1,
                        "Executing the javac compiler failed. The return code is '" + returnCode + "'."));
            }

            return result;
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("The Javac compiler class '" + compilerClass + "' doesn't provide the method " +
                    "compile(String, PrintWriter). Are you sure that you're using a valid Sun JDK?", ex);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("An error occurred while invoking the compile(String, PrintWriter) method of the " +
                    "Javac compiler class '" + compilerClass + "'. Are you sure that you're using a valid Sun JDK?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException("An error occurred while invoking the compile(String, PrintWriter) method of the " +
                    "Javac compiler class '" + compilerClass + "'. Are you sure that you're using a valid Sun JDK?", ex);
        }
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Creates the arguments for the compiler, i.e. it builds an array of arguments that one would pass to
     * the Javac compiler on the command line.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param file       the relative file name of the class you want to compile
     * @return an array of arguments that you have to pass to the Javac compiler
     */
    protected String[] buildCompilerArguments(File sourcePath, File targetPath, File file, ClassLoader classLoader) {
        List<String> arguments = new ArrayList<String>();

        // Note that we're knowingly not specifying the sourcepath as the compiler really should compile
        // only a single file (see 'file'). The dependent classes are available on the classpath anyway.
        // Otherwise the compiler would also compile dependent classes, which we want to avoid! This
        // would result in different versions of a Class file being in use (the system doesn't know that
        // it has to update itself due to a newer version of a Class file whilst the dynamic class loader
        // will already start using it!)
        // arguments.add("-sourcepath");
        // arguments.add(sourcePath.getAbsolutePath());

        // Set the destination / target directory for the compiled .class files.
        arguments.add("-d");
        arguments.add(targetPath.getAbsolutePath());

        // Specify the classpath of the given class loader. This enables the user to write new Java
        // "scripts" that depend on classes that have already been loaded previously. Otherwise he
        // wouldn't be able to use for example classes that are available in a library.
        arguments.add("-classpath");
        arguments.add(ClassLoaderUtils.buildClasspath(classLoader));

        // Enable verbose output.
        arguments.add("-verbose");

        // Generate all debugging information, including local variables.
        arguments.add("-g");

        // Append the source file that is to be compiled. Note that the user specifies only a relative file location.
        arguments.add(file.getAbsolutePath());

        return arguments.toArray(new String[arguments.size()]);
    }

    /**
     * <p>Returns a possibly newly created class loader that you can use in order to load the
     * Javac compiler class. Usually the user would have to put the JAR file
     * '$JAVA_HOME$/lib/tools.jar' on the classpath but this method recognizes this on its own
     * and loads the JAR file if necessary. However, it's not guaranteed that the Javac compiler
     * class is available (e.g. if one is providing a wrong tools.jar file that doesn't contain
     * the required classes).</p>
     *
     * @param toolsJar the location of the JAR file '$JAVA_HOME$/lib/tools.jar' or <code>null</code>
     *                 if you want it to be searched for automatically
     * @return a class loader that you can use in order to load the Javac compiler class
     * @throws MalformedURLException if an error occurred while constructing the URL
     */
    private static ClassLoader createJavacAwareClassLoader(URL toolsJar) throws MalformedURLException {
        // If the user has already included the tools.jar in the classpath we don't have
        // to create a custom class loader as the class is already available.
        if (ClassLoaderUtils.isClassAvailable(JAVAC_MAIN, ClassLoaderUtils.getDefaultClassLoader())) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Seemingly the required JAR file '$JAVA_HOME$/lib/tools.jar' has already been "
                        + "put on the classpath as the class '" + JAVAC_MAIN + "' is present. So there's no "
                        + "need to create a custom class loader for the Javac compiler.");
            }

            return ClassLoaderUtils.getDefaultClassLoader();
        } else {
            // The compiler isn't available in the current classpath, but the user could have specified the tools.jar file.
            if (toolsJar == null) {
                String javaHome = System.getProperty("java.home");
                if (javaHome.toLowerCase().endsWith(File.separator + "jre")) {
                    // Note that even if the user has installed a valid JDK the $JAVA_HOME$ property might reference
                    // the JRE, e.g. '/usr/lib/jvm/java-6-sun-1.6.0.16/jre'. However, in this case we just have to
                    // remove the last four characters (i.e. the '/jre').
                    javaHome = javaHome.substring(0, javaHome.length() - 4);
                }

                // If the user hasn't specified the URL to the tools.jar file, we'll try to find it on our own.
                File toolsJarFile = new File(javaHome, "lib" + File.separatorChar + "tools.jar");
                if (toolsJarFile.exists()) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE,
                                "The required JAR file '$JAVA_HOME$/lib/tools.jar' has been found ['" + toolsJarFile.getAbsolutePath()
                                        + "']. A custom URL class loader will be created for the Javac compiler.");
                    }

                    return new URLClassLoader(
                            new URL[]{toolsJarFile.toURI().toURL()}, ClassLoaderUtils.getDefaultClassLoader());
                } else {
                    throw new IllegalStateException("The Javac compiler class '" + JAVAC_MAIN + "' and the required JAR file " +
                            "'$JAVA_HOME$/lib/tools.jar' couldn't be found. Are you sure that you're using a valid Sun JDK? " +
                            "[$JAVA_HOME$: '" + System.getProperty("java.home") + "']");
                }
            } else {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "The user has specified the required JAR file '$JAVA_HOME$/lib/tools.jar' ['"
                            + toolsJar.toExternalForm() + "']. A custom URL class loader will be created for the Javac compiler.");
                }

                return new URLClassLoader(new URL[]{toolsJar}, ClassLoaderUtils.getDefaultClassLoader());
            }
        }
    }

}
