package org.apache.myfaces.scripting.sandbox.compiler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>This factory chooses and creates an instance of the according compiler
 * implementation based on the current JVM for you. If you're using a Java 6
 * VM, it will return a compiler using the JSR-199 API, otherwise it will
 * return a compiler that uses the JavaC tool.</p>
 *
 */
public class CompilerFactory {

    /**
     * The logger instance for this class.
     */
    private static final Log logger = LogFactory.getLog(CompilerFactory.class);

    // ------------------------------------------ Public methods

    /**
     * <p>Factory method that creates a new Java compiler depending on the
     * Java runtime that this application is running on. That means, if the
     * Java runtime supports the JSR-199 API (i.e. it's at least a Java 6
     * runtime) this API will be used. Otherwise a compiler will be returned
     * that tries to use some internal JDK classes.</p>
     *
     * @return a new Java compiler depending on the Java runtime
     */
   public static Compiler createCompiler() {
        boolean isJava15 = System.getProperty("java.version").indexOf("1.5.") != -1;
      /*  if (!isJava15) {
            // If the Java version is not 1.5, it's safe to assume that
            // it's at least 1.6 as otherwise you wouldn't even be able
            // to load the module (the target VM is set to 1.5 after all).
           return Jsr199CompilerLoader.createJsr199Compiler();
        } else {*/
            if (logger.isWarnEnabled() &&
                    !System.getProperty("java.vendor").contains("Sun Microsystems")) {
                logger.warn("This application is running on a Java runtime that neither supports the JSR-199 API " +
                        "nor is it distributed by Sun Microsystems. However, the compiler implementation that will " +
                        "be used depends on internal classes in the package 'com.sun.tools.javac' so compilation " +
                        "is likely to fail! Be sure that the Java runtime that you're using provides these internal " +
                        "classes!");
            }

            return new JavacCompiler();
        //}
    }

    /**
     * <p>Utility class that loads the JSR-199 compiler implementation. The sole purpose
     * of this class is to ensure that the current class loader doesn't try to load the
     * class 'Jsr199Compiler' until it's sure that we're running on a JVM 6 platform.</p>
     */
 //   private static class Jsr199CompilerLoader {

        /**
         * <p>Utility method that creates a new compiler implementation that uses the
         * JSR-199 Compiler API.</p>
         *
         * @return a new compiler implementation that uses the JSR-199 Compiler API
         */
     //   public static Compiler createJsr199Compiler() {
     //       return new JSF199Compiler();
     //   }

  //  }

}