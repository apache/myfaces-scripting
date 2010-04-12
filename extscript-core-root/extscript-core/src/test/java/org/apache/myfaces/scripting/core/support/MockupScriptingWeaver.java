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

package org.apache.myfaces.scripting.core.support;

import org.apache.myfaces.scripting.api.ScriptingWeaver;

import java.util.Collection;

/**
 * A simple mockup which just
 * remembers its last operation
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class MockupScriptingWeaver implements ScriptingWeaver {

    String _lastOp = null;
    int _scriptingEngine;

    public MockupScriptingWeaver(int scriptingEngine) {
        _scriptingEngine = scriptingEngine;
    }

    public String getLastOp() {
        return _lastOp;
    }

    public void setLastOp(String lastOp) {
        this._lastOp = lastOp;
    }

    public void appendCustomScriptPath(String scriptPath) {
        _lastOp = "appendCustomScriptPath";
    }

    public Object reloadScriptingInstance(Object o, int artifactType) {
        _lastOp = "reloadScriptingInstance";
        return o;
    }

    public Class reloadScriptingClass(Class aclass) {
        _lastOp = "reloadScriptingInstance";
        return aclass;
    }

    public Class loadScriptingClassFromName(String className) {
        _lastOp = "loadScriptingClassFromName";
        return null;
    }

    public int getScriptingEngine() {
        _lastOp = "getScriptingEngine";
        return _scriptingEngine;
    }

    public boolean isDynamic(Class clazz) {
        _lastOp = "isDynamic";
        return true;
    }

    public ScriptingWeaver getWeaverInstance(Class weaverClass) {
        _lastOp = "getWeaverInstance";
        return this;
    }

    public void fullClassScan() {
        _lastOp = "fullClassScan";
    }

    public void fullRecompile() {
        _lastOp = "fullRecompile";
    }

    public void postStartupActions() {
        _lastOp = "postStartupActions";
    }

    public void requestRefresh() {
        _lastOp = "requestRefresh";
    }

    public Collection<String> loadPossibleDynamicClasses() {
        _lastOp = "loadPossibleDynamicClasses";
        return null;
    }

    public void scanForAddedClasses() {
        _lastOp = "scanForAddedClasses";
    }
}
