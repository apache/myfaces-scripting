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
package org.apache.myfaces.scripting.core;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.ProxyUtils;

import java.io.Serializable;

/**
 * @author werpu
 *
 * Facade which holds multiple weavers
 * and implements a chain of responsibility pattern
 * on them
 */
public class ScriptingWeaverHolder implements Serializable,ScriptingWeaver {

    ScriptingWeaver _groovyWeaver = null;
    ScriptingWeaver _javaWeaver = null;

    public ScriptingWeaverHolder(ScriptingWeaver groovyWeaver, ScriptingWeaver javaWeaver) {
        _groovyWeaver = groovyWeaver;
        _javaWeaver = javaWeaver;

    }

    @Override
    public void appendCustomScriptPath(String scriptPaths) {
        throw new RuntimeException("Method not supported from this facade");
    }

    @Override
    public Object reloadScriptingInstance(Object o) {
        if(o.getClass().getName().contains("TestBean2")) {
            System.out.println("Debugpoint found");
        }
        int objectType = ProxyUtils.getEngineType(o.getClass());


        switch (objectType) {
            case ScriptingConst.ENGINE_TYPE_GROOVY:
                return this._groovyWeaver.reloadScriptingInstance(o);
            case ScriptingConst.ENGINE_TYPE_JAVA: //java
                return this._javaWeaver.reloadScriptingInstance(o);
            default: return o;
        }
    }

    @Override
    public Class reloadScriptingClass(Class aclass) {
         if(aclass.getName().contains("TestBean2")) {
            System.out.println("Debugpoint found");
        }

        int objectType = ProxyUtils.getEngineType(aclass);
        switch (objectType) {
            case ScriptingConst.ENGINE_TYPE_GROOVY:
                return this._groovyWeaver.reloadScriptingClass(aclass);
            case ScriptingConst.ENGINE_TYPE_JAVA: //java
                return this._javaWeaver.reloadScriptingClass(aclass);
            default: return aclass;
        }
    }

    @Override
    public Class loadScriptingClassFromName(String className) {
         if(className.contains("TestBean2")) {
            System.out.println("Debugpoint found");
        }
        //we try to load from the chain, we cannot determine the engine type upfront here
        Class retVal = this._groovyWeaver.loadScriptingClassFromName(className);
        if (retVal == null) {
            return this._javaWeaver.loadScriptingClassFromName(className);
        }
        return retVal;
    }

    public ScriptingWeaver get_groovyWeaver() {
        return _groovyWeaver;
    }

    public void set_groovyWeaver(ScriptingWeaver _groovyWeaver) {
        this._groovyWeaver = _groovyWeaver;
    }

    public ScriptingWeaver get_javaWeaver() {
        return _javaWeaver;
    }

    public void set_javaWeaver(ScriptingWeaver _javaWeaver) {
        this._javaWeaver = _javaWeaver;
    }
}
