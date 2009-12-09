package org.apache.myfaces.scripting.loaders.java.jdk5;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.FileUtils;
import org.apache.myfaces.scripting.api.CompilerConst;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;

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
class JavacCompiler implements Compiler {

    /**
     * The logger instance for this class.
     */
    private static final Log logger = LogFactory.getLog(JavacCompiler.class);

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
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("An error occured while trying to load the Javac compiler class.", ex);
        }

        try {
            this.compilerClass = classLoader.loadClass(JAVAC_MAIN);
        } catch (ClassNotFoundException ex) {
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
     * @return the compilation result, i.e. as of now only the compiler output
     */
    public CompilationResult compile(File sourcePath, File targetPath, String classPath) throws CompilationException {
        FileUtils.assertPath(targetPath);

        try {
            StringWriter compilerOutput = new StringWriter();
            // Invoke the Javac compiler
            Method compile = compilerClass.getMethod("compile", new Class[]{String[].class, PrintWriter.class});
            Object[] compilerArguments = new Object[]{buildCompilerArguments(sourcePath, targetPath, classPath), new PrintWriter(compilerOutput)};
            logCommandLine(compilerArguments);

            Integer returnCode = (Integer) compile.invoke(null, compilerArguments);

            CompilationResult result = new CompilationResult(compilerOutput.toString());
            if (returnCode == null || returnCode.intValue() != 0) {
                result.registerError(new CompilationResult.CompilationMessage(-1,
                                                                              "Executing the javac compiler failed. The return code is '" + returnCode + "'." + compilerOutput.toString()));
            }

            return result;
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("The Javac compiler class '" + compilerClass + "' doesn't provide the method " +
                                            "compile(String, PrintWriter). Are you sure that you're using a valid Sun JDK?", ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("An error occured while invoking the compile(String, PrintWriter) method of the " +
                                            "Javac compiler class '" + compilerClass + "'. Are you sure that you're using a valid Sun JDK?", ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("An error occured while invoking the compile(String, PrintWriter) method of the " +
                                            "Javac compiler class '" + compilerClass + "'. Are you sure that you're using a valid Sun JDK?", ex);
        }

    }


    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param file       the relative file name of the class you want to compile
     * @return the compilation result, i.e. as of now only the compiler output
     */
    public CompilationResult compile(File sourcePath, File targetPath, String file, String classPath) throws CompilationException {
        // The destination directory must already exist as javac will not create the destination directory.
        FileUtils.assertPath(targetPath);

        try {
            StringWriter compilerOutput = new StringWriter();

            // Invoke the Javac compiler
            Method compile = compilerClass.getMethod("compile", new Class[]{String[].class, PrintWriter.class});
            if (!targetPath.exists()) {
                targetPath.mkdirs();
            }
            Object[] compilerArguments = new Object[]{buildCompilerArguments(sourcePath, targetPath, file, classPath), new PrintWriter(compilerOutput)};
            logCommandLine(compilerArguments);

            Integer returnCode = (Integer) compile.invoke(null,
                                                          compilerArguments);

            CompilationResult result = new CompilationResult(compilerOutput.toString());
            if (returnCode == null || returnCode.intValue() != 0) {
                result.registerError(new CompilationResult.CompilationMessage(-1,
                                                                              "Executing the javac compiler failed. The return code is '" + returnCode + "'." + compilerOutput.toString()));
            }

            return result;
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("The Javac compiler class '" + compilerClass + "' doesn't provide the method " +
                                            "compile(String, PrintWriter). Are you sure that you're using a valid Sun JDK?", ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("An error occured while invoking the compile(String, PrintWriter) method of the " +
                                            "Javac compiler class '" + compilerClass + "'. Are you sure that you're using a valid Sun JDK?", ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("An error occured while invoking the compile(String, PrintWriter) method of the " +
                                            "Javac compiler class '" + compilerClass + "'. Are you sure that you're using a valid Sun JDK?", ex);
        }
    }

    private void logCommandLine(Object[] compilerArguments) {
        if (logger.isDebugEnabled()) {
            StringBuilder commandLine = new StringBuilder();
            commandLine.append("javac ");
            for (String compilerArgument : (String[]) compilerArguments[0]) {
                commandLine.append(compilerArgument);
                commandLine.append(" ");
            }
            logger.debug(commandLine.toString());
        }
        if (logger.isInfoEnabled()) {
            logger.info("[EXT-SCRIPTING] compiling java");
        }

    }

    // ------------------------------------------ Utility methods

    /**
     * <p/>
     * Creates the arguments for the compiler, i.e. builds up an array of arguments
     * that one would pass to the javac compiler to compile a full path instead of a single file
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @return an array of arguments that you have to pass to the Javac compiler
     */
    protected String[] buildCompilerArguments(File sourcePath, File targetPath, String classPath) {
        List<File> sourceFiles = FileUtils.fetchSourceFiles(sourcePath, "*.java");

        List arguments = getDefaultArguments(sourcePath, targetPath, classPath);

        // Append the source file that is to be compiled. Note that the user specifies only a relative file location.
        for (File sourceFile : sourceFiles) {
            arguments.add(sourceFile.getAbsolutePath());
        }
        return (String[]) arguments.toArray(new String[0]);
    }

    /**
     * <p>Creates the arguments for the compiler, i.e. it builds an array of arguments that one would pass to
     * the Javac compiler on the command line.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param classPath  the classpath for the compiler
     * @param file       the relative file name of the class you want to compile
     * @return an array of arguments that you have to pass to the Javac compiler
     */
    protected String[] buildCompilerArguments(File sourcePath, File targetPath, String file, String classPath) {
        List arguments = getDefaultArguments(sourcePath, targetPath, classPath);

        // Append the source file that is to be compiled. Note that the user specifies only a relative file location.
        arguments.add(new File(sourcePath, file).getAbsolutePath());

        return (String[]) arguments.toArray(new String[0]);
    }

    /**
     * <p>
     * Determination of the default arguments
     * which have to be the same over all
     * different compilation strategies
     * </p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param classPath  the classpath for the compiler
     * @return
     */
    private List getDefaultArguments(File sourcePath, File targetPath, String classPath) {
        List arguments = new ArrayList();

        // Set both the source code path to search for class or interface
        // definitions and the destination directory for class files.
        arguments.add(CompilerConst.JC_SOURCEPATH);
        arguments.add(sourcePath.getAbsolutePath());
        arguments.add(CompilerConst.JC_TARGET_PATH);
        arguments.add(targetPath.getAbsolutePath());
        arguments.add(CompilerConst.JC_CLASSPATH);
        arguments.add(classPath);

        // Enable verbose output.
        arguments.add(CompilerConst.JC_VERBOSE);

        // Generate all debugging information, including local variables.
        arguments.add(CompilerConst.JC_DEBUG);
        return arguments;
    }

    /**
     * <p>Returns a possibly newly created classloader that you can use in order to load the
     * Javac compiler class. Usually the user would have to put the JAR file
     * '$JAVA_HOME$/lib/tools.jar' on the classpath but this method recognizes this on its own
     * and loads the JAR file if necessary. However, it's not guaranteed that the Javac compiler
     * class is available (e.g. if one is providing a wrong tools.jar file that doesn't contain
     * the required classes).</p>
     *
     * @param toolsJar the location of the JAR file '$JAVA_HOME$/lib/tools.jar' or <code>null</code>
     *                 if you want it to be searched for automatically
     * @return a classloader that you can use in order to load the Javac compiler class
     * @throws MalformedURLException if an error occurred while constructing the URL
     */
    private static ClassLoader createJavacAwareClassLoader(URL toolsJar) throws MalformedURLException {
        // If the user has already included the tools.jar in the classpath we don't have
        // to create a custom class loader as the class is already available.
        if (ClassUtils.isPresent(JAVAC_MAIN)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Seemingly the required JAR file '$JAVA_HOME$/lib/tools.jar' has already been "
                             + "put on the classpath as the class '" + JAVAC_MAIN + "' is present. So there's no "
                             + "need to create a custom classloader for the Javac compiler.");
            }

            return ClassUtils.getContextClassLoader();
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
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                "The required JAR file '$JAVA_HOME$/lib/tools.jar' has been found ['" + toolsJarFile.getAbsolutePath()
                                + "']. A custom URL classloader will be created for the Javac compiler.");
                    }

                    return new URLClassLoader(
                            new URL[]{toolsJarFile.toURI().toURL()}, ClassUtils.getContextClassLoader());
                } else {
                    throw new IllegalStateException("The Javac compiler class '" + JAVAC_MAIN + "' and the required JAR file " +
                                                    "'$JAVA_HOME$/lib/tools.jar' couldn't be found. Are you sure that you're using a valid Sun JDK? " +
                                                    "[$JAVA_HOME$: '" + System.getProperty("java.home") + "']");
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("The user has specified the required JAR file '$JAVA_HOME$/lib/tools.jar' ['"
                                 + toolsJar.toExternalForm() + "']. A custom URL classloader will be created for the Javac compiler.");
                }

                return new URLClassLoader(new URL[]{toolsJar}, ClassUtils.getContextClassLoader());
            }
        }
    }

}
