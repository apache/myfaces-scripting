package org.apache.myfaces.scripting.loaders.java.jdk5;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 */
class CompilationResult {

    /** The compiler output */ 
    private String compilerOutput;

    private List<CompilationMessage> errors = new ArrayList<CompilationMessage>();
    private List<CompilationMessage> warnings = new ArrayList<CompilationMessage>();

    // ------------------------------------------ CompilationResult

    public CompilationResult(String compilerOutput) {
        this.compilerOutput = compilerOutput;    
    }

    // ------------------------------------------ Public methods

    public String getCompilerOutput() {
        return compilerOutput;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void registerError(CompilationMessage message) {
        errors.add(message);
    }

    public List<CompilationMessage> getErrors() {
        return errors;
    }

    public void registerWarning(CompilationMessage message) {
        warnings.add(message);
    }

    public List<CompilationMessage> getWarnings() {
        return warnings;
    }

    // ------------------------------------------ Public static classes

    public static class CompilationMessage {

        private long lineNumber;

        private String message;

        public CompilationMessage(long lineNumber, String message) {
            this.lineNumber = lineNumber;
            this.message = message;
        }

        public long getLineNumber() {
            return lineNumber;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format("CompilationMessage[lineNumber='%s', message='%s']", lineNumber, message);
        }
    }


}
