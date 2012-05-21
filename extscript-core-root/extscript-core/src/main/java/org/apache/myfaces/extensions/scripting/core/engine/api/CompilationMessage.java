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

package org.apache.myfaces.extensions.scripting.core.engine.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

/**
 * <p>Utility class that contains all the required information regarding
 * a single compilation message.</p>
 */
public class CompilationMessage
{

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
    public CompilationMessage(long lineNumber, String message)
    {
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
    public long getLineNumber()
    {
        return lineNumber;
    }

    /**
     * <p>Returns the message itself as a string, i.e. the textual content
     * of whatever the compiler complained about.</p>
     *
     * @return the message itself as a string
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * <p>Returns a string representation of this compilation message.</p>
     *
     * @return a string representation of this compilation message
     */
    @Override
    public String toString()
    {
        return String.format(
                "CompilationMessage[lineNumber='%s', message='%s']", lineNumber, message);
    }
}