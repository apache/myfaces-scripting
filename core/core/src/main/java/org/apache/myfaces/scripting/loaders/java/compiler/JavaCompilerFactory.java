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
package org.apache.myfaces.scripting.loaders.java.compiler;

import org.apache.myfaces.scripting.api.*;
import org.apache.myfaces.scripting.api.Compiler;
import org.apache.myfaces.scripting.core.util.ReflectUtil;

/**
 *
 */
public class JavaCompilerFactory {
    /**
     * since the object is stateless
     * declaring it volatile should be enough instead
     * of using synchronized blocks
     * please if you introduce statefulness here
     * we have to add synchronized
     */
    private static volatile JavaCompilerFactory _instance = new JavaCompilerFactory();

    public static JavaCompilerFactory getInstance() {
        if (_instance == null) {
            _instance = new JavaCompilerFactory();
        }
        return _instance;
    }

    private String getScriptingFacadeClass(boolean allowJSR) {
        String javaVer = System.getProperty("java.version");
        String[] versionArr = javaVer.split("\\.");

        int major = Integer.parseInt(versionArr[Math.min(versionArr.length, 1)]);

        if (major > 5 && allowJSR) {
            //jsr199 compliant jdk
            return ScriptingConst.JSR199_COMPILER;
        }
        //otherwise
        return ScriptingConst.JAVA5_COMPILER;
    }

    public org.apache.myfaces.scripting.api.Compiler getCompilerInstance() {
        return (Compiler) ReflectUtil.instantiate(getScriptingFacadeClass(true));
    }

    public org.apache.myfaces.scripting.api.Compiler getCompilerInstance(boolean allowJSR) {
        return (Compiler) ReflectUtil.instantiate(getScriptingFacadeClass(allowJSR));
    }

}
