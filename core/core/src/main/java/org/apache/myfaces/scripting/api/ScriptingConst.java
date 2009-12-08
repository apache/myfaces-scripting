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

    public static final int ENGINE_TYPE_ALL = -2;
    public static final int ENGINE_TYPE_NO_ENGINE = -1;
    public static final int ENGINE_TYPE_GROOVY = 0;
    public static final int ENGINE_TYPE_JAVA = 1;
    public static final int ARTEFACT_TYPE_UNKNOWN = -1;
    public static final int ARTEFACT_TYPE_MANAGEDBEAN = 1;
    public static final int ARTEFACT_TYPE_MANAGEDPROPERTY = 2;
    public static final int ARTEFACT_TYPE_RENDERKIT = 3;
    public static final int ARTEFACT_TYPE_VIEWHANDLER = 4;
    public static final int ARTEFACT_TYPE_RENDERER = 5;
    public static final int ARTEFACT_TYPE_COMPONENT = 6;
    public static final int ARTEFACT_TYPE_VALIDATOR = 7;
    public static final int ARTEFACT_TYPE_BEHAVIOR = 8;
    public static final int ARTEFACT_TYPE_APPLICATION = 9;
    public static final int ARTEFACT_TYPE_ELCONTEXTLISTENER = 10;
    public static final int ARTEFACT_TYPE_ACTIONLISTENER = 11;
    public static final int ARTEFACT_TYPE_VALUECHANGELISTENER = 12;
    public static final int ARTEFACT_TYPE_CONVERTER = 13;
    public static final int ARTEFACT_TYPE_LIFECYCLE = 14;
    public static final int ARTEFACT_TYPE_PHASELISTENER = 15;
    public static final int ARTEFACT_TYPE_FACESCONTEXT = 16;
    public static final int ARTEFACT_TYPE_NAVIGATIONHANDLER = 17;
    public static final int ARTEFACT_TYPE_RESPONSEWRITER = 18;
    public static final int ARTEFACT_TYPE_RESPONSESTREAM = 19;
    public static final int ARTEFACT_TYPE_RESOURCEHANDLER = 19;
    public static final int ARTEFACT_TYPE_CLIENTBEHAVIORRENDERER = 20;

    public static final String JAVA_WILDCARD = "*.java ";
}
