package org.apache.myfaces.scripting.loaders.java.jdk5;

import org.apache.myfaces.extensions.scripting.compiler.CompilationResult;

import java.io.File;

/**
 *
 */
public interface Compiler {

    /**
     * <p>Compiles the given file and creates an according class file in the given target path.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param file       the relative file name of the class you want to compile
     * @return the compilation result, i.e. the compiler output, a list of errors and a list of warnings
     */
    public CompilationResult compile(File sourcePath, File targetPath, String file, String classPath) throws CompilationException;

    /**
     * <p>Compiles the given sources and creates an according class files in the given target path.</p>
     *
     * @param sourcePath the path to the source directory
     * @param targetPath the path to the target directory
     * @param classPath  the classpath for the compiler
     * @return the compilation result, i.e. the compiler output, a list of errors and a list of warnings
     */
    public CompilationResult compile(File sourcePath, File targetPath, String classPath) throws CompilationException;

}
