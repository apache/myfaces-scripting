package org.apache.myfaces.extensions.scripting.cdi.context;

import org.apache.myfaces.extensions.scripting.cdi.compiler.CompilationException;
import org.apache.myfaces.extensions.scripting.cdi.compiler.CompilationResult;
import org.apache.myfaces.extensions.scripting.cdi.compiler.Compiler;
import org.apache.myfaces.extensions.scripting.cdi.loaders.ReloadingClassLoader;
import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.Resource;
import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.ResourceMonitor;

import java.io.File;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class CompilationResourceMonitor implements ResourceMonitor {

    private static final Logger logger = Logger.getLogger(CompilationResourceMonitor.class.getName());

    private ReloadingClassLoader classLoader;

    private Compiler compiler;

    private File sourcePath;
    private File targetPath;

    // -------------------------------------- Constructors

    public CompilationResourceMonitor(
            ReloadingClassLoader classLoader, Compiler compiler, File sourcePath, File targetPath) {
        this.classLoader = classLoader;
        this.compiler = compiler;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }


    // -------------------------------------- ResourceMonitor methods

    @Override
    public boolean resourceModified(Resource resource) {
        try {
            File file = resource.getFile();
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Found a more recent source file '"
                        + file.getName() + "' which the compilation monitor will compile now.");
            }

            CompilationResult result = compiler.compile(sourcePath, targetPath, file, classLoader);
            if (result.hasErrors()) {
                logger.severe("An error occurred while compiling the source file '"
                        + file.getAbsolutePath() + "': Errors: '" + result.getErrors());
            } else {
                String className = buildClassName(sourcePath, file);
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("The reloading class loader is going to be told to reloaded the class '"
                            + className + "'.");
                }

                classLoader.reloadClass(className);
            }
        } catch (CompilationException ex) {
            logger.log(Level.SEVERE, "An error occurred while compiling the source file '" + resource + "'.", ex);
        }

        return true;
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Builds the name of a class given the relative location of its source file, i.e.
     * if you've got the source file com/acme/Test.java this method will tell you that
     * the name of the class is "com.acme.Test". In order to specify the relative location
     * of the source file, you have to provide two absolute paths: the path to the source
     * file and the path to the root source path.</p>
     *
     * @param rootSourcePath the root source path containing all Java source files
     * @param sourceFile the Java source file containing the class you want to retrieve the name for
     *
     * @return the name of the class that the given source file contains
     */
    private String buildClassName(File rootSourcePath, File sourceFile) {
        Stack classNameElements = new Stack();

        // At first strip the ".java" extension from the file name.
        classNameElements.push(
                sourceFile.getName().substring(0, sourceFile.getName().indexOf(".java")));

        // Walk up the hierarchy and save the directory names in a stack.
        sourceFile = sourceFile.getParentFile();
        while (!rootSourcePath.equals(sourceFile)) {
            classNameElements.push(sourceFile.getName());
            sourceFile = sourceFile.getParentFile();
        }

        // Pop one directory name after another from the stack to determine the class name.
        StringBuffer className = new StringBuffer();
        while (!classNameElements.empty()) {
            className.append(classNameElements.pop());

            // If we're dealing with the last element we shouldn't add the "." anymore,
            // otherwise we'll end up with a class name like "com.acme.Test."
            if (!classNameElements.empty()) {
                className.append(".");
            }
        }

        return className.toString();
    }

}
