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
package org.apache.myfaces.otherEngines;

import org.apache.commons.io.FileUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is only a demonstration application on how to implement
 * managed beans in other engines using the java scripting api
 * We do not really support it but feel free to apply the
 * techniques used here
 */
public class JavascriptProxyFactory implements InvocationHandler {

    static ScriptEngine _engine = null;

    static {
        ScriptEngineManager manager = new ScriptEngineManager();
        _engine = manager.getEngineByName("JavaScript");
    }

    static AtomicInteger _instanceIncr = new AtomicInteger(0);
    String _jsInstance;
    Object _jsProxy;
    String _script;

    protected JavascriptProxyFactory(String classDef, String script) throws ScriptException {
        int currCnt = _instanceIncr.getAndIncrement();
        _jsInstance = "myVar_" + currCnt;

        this._script = script + " var " + _jsInstance + " = new " + classDef + "();";
        _engine.eval(this._script);
        _jsProxy = _engine.get(_jsInstance);

    }

    public static synchronized Object newInstance(Class theInterface, String jsClass, File script) throws ScriptException {

        try {
            return java.lang.reflect.Proxy.newProxyInstance(theInterface.getClassLoader(), new Class[]{theInterface}, new JavascriptProxyFactory(jsClass, FileUtils.readFileToString(script)));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
        Invocable inv = (Invocable) _engine;
        return inv.invokeMethod(_jsProxy, method.getName(), args);
    }
}
