package org.apache.myfaces.extensions.scripting.compiler.support;

import org.apache.myfaces.extensions.scripting.compiler.CompilationException;
import org.apache.myfaces.extensions.scripting.compiler.CompilationResult;
import org.apache.myfaces.extensions.scripting.compiler.Compiler;

import java.io.File;

/**
 *
 */
public abstract class CompilationErrorHandler implements Compiler {

    /** The compiler that this error handler is delegating to. */
    private Compiler compiler;

    // ------------------------------------------ Constructors

    /**
     * <p>Constructs a new instance of this class using the given compiler as delegate. That
     * means that this object behaves like a compiler by delegating all compile requests to
     * the given compiler, but additionally it enables you to handle compilation errors and
     * exceptions.</p>
     *
     * @param compiler the compiler that this error handler is delegating to
     */
    public CompilationErrorHandler(Compiler compiler) {
        if (compiler == null) {
            throw new IllegalArgumentException(
                    "The given compiler must not be null.");
        }

        this.compiler = compiler;
    }

    // ------------------------------------------ Compiler methods


    /**
     * 
     *
     * @param sourcePath  the path to the source directory
     * @param targetPath  the path to the target directory
     * @param file        the file of the class you want to compile
     * @param classLoader the class loader for dependent classes
     * @return the compilation result, i.e. the compiler output, a list of errors and a list of warnings
     * @throws CompilationException
     *          if a severe error occurred while trying to compile a file
     */
    public CompilationResult compile(File sourcePath, File targetPath, File file, ClassLoader classLoader) throws CompilationException {
        try {
            CompilationResult result = compiler.compile(sourcePath, targetPath, file, classLoader);
            if (result.hasErrors()) {
                result = handleCompilationError(result);
            }
            
            return result;
        } catch (CompilationException ex) {
            return handleCompilationException(ex);
        }
    }

    /**
     * 
     *
     * @param sourcePath  the path to the source directory
     * @param targetPath  the path to the target directory
     * @param file        the relative file name of the class you want to compile
     * @param classLoader the class loader for dependent classes
     * @return the compilation result, i.e. the compiler output, a list of errors and a list of warnings
     * @throws CompilationException
     *          if a severe error occurred while trying to compile a file
     */
    public CompilationResult compile(File sourcePath, File targetPath, String file, ClassLoader classLoader) throws CompilationException {
        try {
            CompilationResult result = compiler.compile(sourcePath, targetPath, file, classLoader);
            if (result.hasErrors()) {
                result = handleCompilationError(result);
            }

            return result;
        } catch (CompilationException ex) {
            return handleCompilationException(ex);
        }
    }

    // ------------------------------------------ Template methods

    /**
     * <p></p>
     *
     * @param result
     * @return
     */
    protected abstract CompilationResult handleCompilationError(CompilationResult result);

    /**
     * <p>Tries to handle the given compilation exception somehow (e.g. log the cause of the compilation
     * error, ..). If, however, it's not able to handle the exception, it just rethrows it again.</p>
     *
     * @param ex the exception that occurred while trying to compile a file
     *
     * @return the compilation result after handling the compilation exception
     *
     * @throws CompilationException if this method cannot handle the given exception
     */
    protected abstract CompilationResult handleCompilationException(CompilationException ex)
            throws CompilationException;

}
