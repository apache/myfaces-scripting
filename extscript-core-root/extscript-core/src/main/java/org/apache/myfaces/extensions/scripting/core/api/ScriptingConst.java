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

package org.apache.myfaces.extensions.scripting.core.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * common constants shared amont the scriping configuration
 */

public class ScriptingConst
{

    public static final String SCRIPTING_CLASSLOADER = "org.apache.myfaces.extensions.SCRIPTING_CLASSLOADER";
    public static final String SCRIPTING_GROOVFACTORY = "org.apache.myfaces.extensions.SCRIPTING_GROOVYFACTORY";
    public static final String SCRIPTING_REQUSINGLETON = "org.apache.myfaces.extensions.SCRIPTING_REQUSINGLETON";

    public static final String INIT_PARAM_SCRIPTING_PACKAGE_WHITELIST = "org.apache.myfaces.extensions.scripting.PGK_WHITELIST";
    public static final String INIT_PARAM_SCRIPTING_ADDITIONAL_CLASSPATH = "org.apache.myfaces.extensions.scripting.ADDITIONAL_CLASSPATH";
    public static final String INIT_PARAM_RESOURCE_PATH = "org.apache.myfaces.extensions.scripting.resources.LOADER_PATHS";
    public static final String INIT_PARAM_CUSTOM_GROOVY_LOADER_PATHS = "org.apache.myfaces.extensions.scripting.groovy.LOADER_PATHS";
    public static final String INIT_PARAM_CUSTOM_SCALA_LOADER_PATHS = "org.apache.myfaces.extensions.scripting.scala" +
            ".LOADER_PATHS";
    //TODO add this


    public static final String INIT_PARAM_CUSTOM_JAVA_LOADER_PATHS = "org.apache.myfaces.extensions.scripting.java.LOADER_PATHS";
    public static final String INIT_PARAM_INITIAL_COMPILE="org.apache.myfaces.extensions.scripting.INITIAL_COMPILE_AND_SCAN";
    public static final String INIT_PARAM_MYFACES_PLUGIN = "org.apache.myfaces.FACES_INIT_PLUGINS";

    public static final String EXT_VAL_MARKER="org.apache.myfaces.extensions.validator";

    public static final String CONTEXT_VALUE_DIVIDER = ",";

    public static final String RELOAD_MAP = "reloadMap";
    public static final String SESS_BEAN_REFRESH_TIMER = "sessbeanrefrsh";

    public static final int TAINT_INTERVAL = 2000;

    public static final int ENGINE_TYPE_JSF_ALL = -2;
    public static final int ENGINE_TYPE_JSF_NO_ENGINE = -1;
    public static final int ENGINE_TYPE_JSF_GROOVY = 0;
    public static final int ENGINE_TYPE_JSF_JAVA = 1;
    public static final int ENGINE_TYPE_JSF_SCALA = 2;
    public static final int ARTIFACT_TYPE_UNKNOWN = -1;
    public static final int ARTIFACT_TYPE_MANAGEDBEAN = 1;
    public static final int ARTIFACT_TYPE_MANAGEDPROPERTY = 2;
    public static final int ARTIFACT_TYPE_RENDERKIT = 3;
    public static final int ARTIFACT_TYPE_VIEWHANDLER = 4;
    public static final int ARTIFACT_TYPE_RENDERER = 5;
    public static final int ARTIFACT_TYPE_COMPONENT = 6;
    public static final int ARTIFACT_TYPE_VALIDATOR = 7;
    public static final int ARTIFACT_TYPE_BEHAVIOR = 8;
    public static final int ARTIFACT_TYPE_APPLICATION = 9;
    public static final int ARTIFACT_TYPE_ELCONTEXTLISTENER = 10;
    public static final int ARTIFACT_TYPE_ACTIONLISTENER = 11;
    public static final int ARTIFACT_TYPE_VALUECHANGELISTENER = 12;
    public static final int ARTIFACT_TYPE_CONVERTER = 13;
    public static final int ARTIFACT_TYPE_LIFECYCLE = 14;
    public static final int ARTIFACT_TYPE_PHASELISTENER = 15;
    public static final int ARTIFACT_TYPE_FACESCONTEXT = 16;
    public static final int ARTIFACT_TYPE_NAVIGATIONHANDLER = 17;
    public static final int ARTIFACT_TYPE_RESPONSEWRITER = 18;
    public static final int ARTIFACT_TYPE_RESPONSESTREAM = 19;
    public static final int ARTIFACT_TYPE_RESOURCEHANDLER = 19;
    public static final int ARTIFACT_TYPE_CLIENTBEHAVIORRENDERER = 20;
    public static final int ARTIFACT_TYPE_SYSTEMEVENTLISTENER = 21;

    //faclets artifacts
    public static final int ARTIFACT_TYPE_TAG_HANDLER = 22;
    public static final int ARTIFACT_TYPE_COMPONENT_HANDLER = 23;
    public static final int ARTIFACT_TYPE_VALIDATOR_HANDLER = 24;
    public static final int ARTIFACT_TYPE_CONVERTER_HANDLER = 25;
    public static final int ARTIFACT_TYPE_BEHAVIOR_HANDLER = 26;

    public static final String CTX_ATTR_REQUEST_CNT = "RequestCnt";
    public static final String CTX_ATTR_CONFIGURATION = "ExtScriptingConfig";
    public static final String CTX_ATTR_STARTUP = "ExtScriptingStartup";
    public static final String CTX_ATTR_SCRIPTING_WEAVER = "ScriptingWeaver";
    public static final String CTX_ATTR_REFRESH_CONTEXT = "RefreshContext";
    public static final String CTX_ATTR_EXTENSION_EVENT_SYSTEM = "ExtEventSystem";

    public static final String FILE_EXTENSION_GROOVY = ".groovy";
    public static final String GROOVY_FILE_ENDING = ".groovy";
    public static final String JAVA_FILE_ENDING = ".java";
    public static final String SCALA_FILE_ENDING = ".scala";
    public static final String JSR199_COMPILER = "org.apache.myfaces.extensions.scripting.loaders.java.jsr199.JSR199Compiler";
    public static final String JAVA5_COMPILER = "org.apache.myfaces.extensions.scripting.loaders.java.compiler.JavacCompiler";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_APPLICATION = "application";
    public static final String SCOPE_REQUEST = "request";
    public static final String GROOVY_SOURCE_ROOT = "/WEB-INF/groovy/";
    public static final String SCALA_SOURCE_ROOT = "/WEB-INF/scala/";

    public static final String JAVA_SOURCE_ROOT = "/WEB-INF/java/";
    public static final String ERR_SERVLET_FILTER = "[EXT-SCRIPTING] The servlet filter has not been set, please check your web.xml for following entries:" +
            "\n    <filter>\n" +
            "        <filter-name>scriptingFilter</filter-name>\n" +
            "        <filter-class>org.apache.myfaces.extensions.scripting.servlet.ScriptingServletFilter</filter-class>\n" +
            "    </filter>\n" +
            "    <filter-mapping>\n" +
            "        <filter-name>scriptingFilter</filter-name>\n" +
            "        <url-pattern>/*</url-pattern>\n" +
            "        <dispatcher>REQUEST</dispatcher>\n" +
            "        <dispatcher>FORWARD</dispatcher>\n" +
            "        <dispatcher>INCLUDE</dispatcher>\n" +
            "        <dispatcher>ERROR</dispatcher>\n" +
            "    </filter-mapping>";
    static final String EXT_VAL_REQ_KEY = "org.apache.myfaces.extension.scripting.clearExtvalCache_Done";
    public static final String JAVAX_FACES = "javax.faces";
}
