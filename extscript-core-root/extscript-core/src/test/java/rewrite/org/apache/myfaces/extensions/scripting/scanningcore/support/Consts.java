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

package rewrite.org.apache.myfaces.extensions.scripting.scanningcore.support;

import static org.apache.myfaces.extensions.scripting.api.ScriptingConst.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class Consts {
    public static final String PROBE2 = "org.apache.myfaces.extensions.scripting.core.classIdentifier.Probe2";
    public static String JAVA_FILE_ENDING = ".java";
    public static final int[] ARTIFACT_TYPES = {
            ARTIFACT_TYPE_UNKNOWN,
            ARTIFACT_TYPE_MANAGEDBEAN,
            ARTIFACT_TYPE_MANAGEDPROPERTY,
            ARTIFACT_TYPE_RENDERKIT,
            ARTIFACT_TYPE_VIEWHANDLER,
            ARTIFACT_TYPE_RENDERER,
            ARTIFACT_TYPE_COMPONENT,
            ARTIFACT_TYPE_VALIDATOR,
            ARTIFACT_TYPE_BEHAVIOR,
            ARTIFACT_TYPE_APPLICATION,
            ARTIFACT_TYPE_ELCONTEXTLISTENER,
            ARTIFACT_TYPE_ACTIONLISTENER,
            ARTIFACT_TYPE_VALUECHANGELISTENER,
            ARTIFACT_TYPE_CONVERTER,
            ARTIFACT_TYPE_LIFECYCLE,
            ARTIFACT_TYPE_PHASELISTENER,
            ARTIFACT_TYPE_FACESCONTEXT,
            ARTIFACT_TYPE_NAVIGATIONHANDLER,
            ARTIFACT_TYPE_RESPONSEWRITER,
            ARTIFACT_TYPE_RESPONSESTREAM,
            ARTIFACT_TYPE_RESOURCEHANDLER,
            ARTIFACT_TYPE_CLIENTBEHAVIORRENDERER,
            ARTIFACT_TYPE_SYSTEMEVENTLISTENER,
    };
    public static final String JAVA_LANG = "java.lang";
}
