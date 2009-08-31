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


import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.ProxyUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Generic artefact invocation handler
 * which should be able to cover
 * all interfacable artefacts which
 * only have reloading logic
 * and can cope with reloading on method level
 *
 * @author Werner Punz
 */
public class MethodLevelReloadingHandler implements InvocationHandler, Serializable, Decorated {
    Class _loadedClass = null;
    transient Object _delegate = null;
    ScriptingWeaver _weaver = null;

    public MethodLevelReloadingHandler(Object rootObject) {
        _loadedClass = rootObject.getClass();
        _delegate = rootObject;
    }


    public Object getDelegate() {
        return _delegate;
    }

    public Class getLoadedClass() {
        return _loadedClass;
    }

    public void setLoadedClassName(Class loadedClass) {
        this._loadedClass = loadedClass;
    }

    /**
     * outside interface to the invoke method
     * which gets called every time a method
     * is called
     *
     * @param o
     * @param method
     * @param objects
     * @return
     * @throws Throwable
     */
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return reloadInvoke(method, objects);
    }


    /**
     * invoke handler which is triggered
     * by every method call which takes care of the reload
     *
     * @param method  the method to call
     * @param objects the params
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    protected Object reloadInvoke(Method method, Object[] objects) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (_weaver == null)
            _weaver = ProxyUtils.getWeaver();

        if (_delegate == null) {
            //stateless or lost state due to a lifecycle iteration we trigger anew
            _delegate = (_weaver.reloadScriptingClass(_loadedClass)).newInstance();
        } else {
            //if we are stateful only a tainted artefact is reloaded
            _delegate = _weaver.reloadScriptingInstance(_delegate);
        }
        //check for proxies and unproxy them before calling the methods
        //to avoid unneccessary cast problems
        //this is slow on long param lists but it is better
        //to be slow than to have casts an calls in the code
        //for production we can compile the classes anyway and avoid
        //this
        unmapProxies(objects);
        return method.invoke(_delegate, objects);
    }

    /**
     * unmap proxied objects
     *
     * @param objects
     */
    private void unmapProxies(Object[] objects) {
        if (objects == null) return;
        for (int cnt = 0; cnt < objects.length; cnt++) {
            objects[cnt] = ProxyUtils.getDelegateFromProxy(objects[cnt]);
        }
    }
}
