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
package org.apache.myfaces.scripting.api;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Contains all information regarding the result of a particular compilation process.</p>
 */
public class CompilationResult {

    /**
     * The compiler output, i.e. simply everything that the compiler would usually
     * print to the console, if you executed the same process on the command line
     * instead.
     */
    private String compilerOutput;

    /**
     * A list of error messages that the compiler has produced. Note that if there
     * are no error messages, it's safe to assume that compilation succeeded.
     */
    private List<CompilationMessage> errors;

    /**
     * A list of warnings that the compiler has produced.
     */
    private List<CompilationMessage> warnings;

    // ------------------------------------------ Constructors

    /**
     * <p>Constructs a new compilation result object using the compiler output. However,
     * note that this constructor doesn't attempt to parse the compiler output to get the
     * error messages and warnings. You'll have to register those messages yourself
     * afterwards.</p>
     *
     * @param compilerOutput the compiler output, i.e. simply everything that the compiler would
     *                       usually print to the console, if you executed the same process on
     *                       the command line instead
     */
    public CompilationResult(String compilerOutput) {
        this.compilerOutput = compilerOutput;

        this.errors = new ArrayList<CompilationMessage>();
        this.warnings = new ArrayList<CompilationMessage>();
    }

    // ------------------------------------------ Public methods

    /**
     * <p>Returns the compiler output, i.e. simply everything that the compiler would usually
     * print to the console, if you executed the same process on the command line
     * instead.</p>
     *
     * @return the compiler output
     */
    public String getCompilerOutput() {
        return compilerOutput;
    }

    /**
     * <p>Determines whether any error messages have been registered, i.e. whether compilation
     * was successful.</p>
     *
     * @return <code>true</code if no error messages have been registered, i.e. if compilation
     *         was sucessful; <code>false</code> otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * <p>Registers the given message as an error message.</p>
     *
     * @param message the error message you want to register
     */
    public void registerError(CompilationMessage message) {
        if (message != null) {
            errors.add(message);
        }
    }

    /**
     * <p>Returns a list of error messages that the compiler has produced,
     * i.e. the error messages that have been registered previously.</p>
     *
     * @return a list of error messages
     */
    public List<CompilationMessage> getErrors() {
        return errors;
    }

    /**
     * <p>Registers the given message as a warning. You can register as many warnings as you want
     * and it won't affect whether compilation was sucessful or not.</p>
     *
     * @param message the warning you want to register
     */
    public void registerWarning(CompilationMessage message) {
        if (message != null) {
            warnings.add(message);
        }
    }

    /**
     * <p>Returns a list of warnings that the compiler has produced,
     * i.e. the warnings that have been registered previously.</p>
     *
     * @return a list of warnings
     */
    public List<CompilationMessage> getWarnings() {
        return warnings;
    }

    // ------------------------------------------ Public static classes

    /**
     * <p>Utility class that contains all the required information regarding
     * a single compilation message.</p>
     */
    public static class CompilationMessage {

        /**
         * the line number of this compilation message
         */
        private long lineNumber;

        /**
         * the actual compilation message
         */
        private String message;

        // -------------------------------------- Constructors

        /**
         * <p>Constructs a new compilation message using the line number
         * and the actual compilation message as a string.</p>
         *
         * @param lineNumber the line number
         * @param message    the actual compilation message
         */
        public CompilationMessage(long lineNumber, String message) {
            this.lineNumber = lineNumber;
            this.message = message;
        }

        // -------------------------------------- Public methods

        /**
         * <p>The number of the relevant line where this warning or error
         * has occured, or <code>-1</code> if it is not known.</p>
         *
         * @return the line number
         */
        public long getLineNumber() {
            return lineNumber;
        }

        /**
         * <p>Returns the message itself as a string, i.e. the textual content
         * of whatever the compiler complained about.</p>
         *
         * @return the message itself as a string
         */
        public String getMessage() {
            return message;
        }

        /**
         * <p>Returns a string representation of this compilation message.</p>
         *
         * @return a string representation of this compilation message
         */
        @Override
        public String toString() {
            return String.format(
                    "CompilationMessage[lineNumber='%s', message='%s']", lineNumber, message);
        }
    }

}
