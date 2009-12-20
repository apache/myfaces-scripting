package org.apache.myfaces.extensions.scripting.compiler;

import org.apache.myfaces.extensions.scripting.loader.ClassLoaderUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 *
 */
public class GroovyCompiler implements Compiler {

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
        StringWriter compilerOutput = new StringWriter();

        CompilationUnit compilationUnit = new CompilationUnit(
                buildCompilerConfiguration(sourcePath, targetPath, file, classLoader));
        compilationUnit.getConfiguration().setOutput(new PrintWriter(compilerOutput));
        compilationUnit.addSource(file);
        compilationUnit.compile();

        return new CompilationResult(compilerOutput.toString());
    }

    // ------------------------------------------ Utility methods

    protected CompilerConfiguration buildCompilerConfiguration(File sourcePath, File targetPath, File file, ClassLoader classLoader) {
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
