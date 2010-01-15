package org.apache.myfaces.scripting.loaders.java.jdk5;

/**
 *
 */
public class CompilationException extends Exception {

    // ------------------------------------------ Constructors

    public CompilationException(String message) {
        super(message);
    }

    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompilationException(Throwable cause) {
        super(cause);
    }
}
