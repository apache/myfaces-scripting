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
 * Various constants shared over the various compiler implementations
 * JSR or non JSR!
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class CompilerConst {
    public static final String STD_ERROR_HEAD = "Java Compiler, error on line: ";
    public static final String STD_WARN_HEAD = "Java Compiler, warning on line: ";
    public static final String STD_MANDATORY_WARN_HEAD = "Java Compiler, mandatory warning on line: ";
    public static final String STD_OTHER_HEAD = "Java Compiler, other diagnostic on line: ";
    public static final String STD_NOTE_HEAD = "Java Compiler, Info on line: ";
    public static final String JC_CLASSPATH = "-cp";
    public static final String JC_TARGET_PATH = "-d";
    public static final String JC_SOURCEPATH = "-sourcepath";
    public static final String JC_DEBUG = "-g";
    public static final String JAVA_WILDCARD = "*.java ";
    public static final String JRUBY_WILDARD = "*.rb";

    public static final String JC_VERBOSE = "-verbose";
    @SuppressWarnings("unused")
    public static final String JC_SOURCE = "-source";
}
