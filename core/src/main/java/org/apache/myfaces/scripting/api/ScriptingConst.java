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

/**
 * Various constants being used by the
 * system
 *
 * @author Werner Punz
 */
public class ScriptingConst {
    public static final String SCRIPTING_CLASSLOADER = "org.apache.myfaces.SCRIPTING_CLASSLOADER";
    public static final String SCRIPTING_GROOVFACTORY = "org.apache.myfaces.SCRIPTING_GROOVYFACTORY";
    public static final String SCRIPTING_REQUSINGLETON = "org.apache.myfaces.SCRIPTING_REQUSINGLETON";

    public static final String RELOAD_MAP = "reloadMap";
    public static final int TAINT_INTERVAL = 2000;

    public static final int ENGINE_TYPE_NO_ENGINE = -1;
    public static final int ENGINE_TYPE_GROOVY = 0;
    public static final int ENGINE_TYPE_JAVA = 1;
}
