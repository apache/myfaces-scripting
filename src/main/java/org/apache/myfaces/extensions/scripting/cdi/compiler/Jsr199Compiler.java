package org.apache.myfaces.extensions.scripting.cdi.compiler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.extensions.scripting.cdi.utils.ClassLoaderUtils;

import javax.tools.*;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A compiler implementation that utilizes the JSR-199 API. Note that you have to use
 * at least Java 6 in order to both use and compile this class.</p>
 */
public class Jsr199Compiler implements Compiler {

    /**
     * The logger instance for this class.
     */
    private static final Log logger = LogFactory.getLog(Jsr199Compiler.class);

    private JavaCompiler compiler;

    // ------------------------------------------ Constructors

    public Jsr199Compiler() {
        this(ToolProvider.getSystemJavaCompiler());
    }

    public Jsr199Compiler(JavaCompiler compiler) {
        this.compiler = compiler;
    }

    // ------------------------------------------ Compiler methods

    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param file       the file of the class you want to compile
     * @return the compilation result, i.e. as of now only the compiler output
     */
    public CompilationResult compile(File sourcePath, File targetPath, String file, ClassLoader classLoader) throws CompilationException {
        return compile(sourcePath, targetPath, new File(sourcePath, file), classLoader);
    }

    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param file       the relative file name of the class you want to compile
     * @return
     */
    public CompilationResult compile(File sourcePath, File targetPath, File file, ClassLoader classLoader) throws CompilationException {
        // The destination directory must already exist as the JSR-199 compiler will not create the destination directory.
        if (!targetPath.exists()) {
            if (!targetPath.mkdirs()) {
                throw new CompilationException("It wasn't possible to create the target " +
                        "directory for the compiler ['" + targetPath.getAbsolutePath() + "'].");
            }

            // If we've created the destination directory, we'll delete it as well once the application exits
            targetPath.deleteOnExit();
        }

        try {
            StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);

            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
            StringWriter compilerOutput = new StringWriter();

            JavaCompiler.CompilationTask compilationTask =
                    compiler.getTask(compilerOutput, manager, collector,
                            buildCompilerOptions(sourcePath, targetPath, classLoader),
                            null, manager.getJavaFileObjects(file));
            compilationTask.call();

            CompilationResult result = new CompilationResult(compilerOutput.getBuffer().toString());
            for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                CompilationResult.CompilationMessage message = new CompilationResult.CompilationMessage(
                        diagnostic.getLineNumber(), diagnostic.getMessage(null));

                if (Diagnostic.Kind.ERROR.equals(diagnostic.getKind())) {
                    result.registerError(message);
                }
                else if (Diagnostic.Kind.WARNING.equals(diagnostic.getKind()) ||
                        Diagnostic.Kind.MANDATORY_WARNING.equals(diagnostic.getKind())) {
                    result.registerWarning(message);
                }
            }

            return result;
        }
        catch (IllegalArgumentException ex) {
            throw new CompilationException("Any of the given compilation units are of other kind than source.", ex);
        }
        catch (RuntimeException ex) {
            throw new CompilationException("An unrecoverable error occured in a user supplied component.", ex);
        }
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Creates the options for the compiler, i.e. it builds an array of arguments that one would pass to
     * the Javac compiler on the command line. Note that this method only includes the compiler options, i.e.
     * it doesn't specify the location of the source files that the compiler should compile.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @return an array of options that you have to pass to the Javac compiler
     */
    protected List<String> buildCompilerOptions(File sourcePath, File targetPath, ClassLoader classLoader) {
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

        // Specify the classpath of the given classloader. This enables the user to write new Java
        // "scripts" that depend on classes that have already been loaded previously. Otherwise he
        // wouldn't be able to use for example classes that are available in a library.
        arguments.add("-classpath");
        arguments.add(ClassLoaderUtils.buildClasspath(classLoader));

        // Enable verbose output.
        arguments.add("-verbose");

        // Generate all debugging information, including local variables.
        arguments.add("-g");

        return arguments;
    }

}