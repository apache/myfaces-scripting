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
package org.apache.myfaces.extensions.scripting.core.reloading;


import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Generic artifact invocation handler
 * which should be able to cover
 * all interfaceable artifacts which
 * only have reloading logic
 * and can cope with reloading on method level
 * <p>&nbsp;</p>
 * Note this works only for a minority of the artifacts
 * the reason, most artifacts do not rely on interfaces but
 * on base classes
 *
 * @author Werner Punz
 */
@SuppressWarnings("unused")
public class MethodLevelReloadingHandler extends ReloadingInvocationHandler implements Serializable {

    private static final long serialVersionUID = -3034995032644947216L;

    int _artifactType;

    public MethodLevelReloadingHandler(Object rootObject, int artifactType) {
        _loadedClass = rootObject.getClass();
        _delegate = rootObject;
        _artifactType = artifactType;
    }

    /**
     * outside interface to the invoke method
     * which gets called every time a method
     * is called
     *
     * @param object       the object holding the method
     * @param method       the method
     * @param paramHolders the param holders
     * @return the return value of the operation
     * @throws Throwable in case of an error
     */
    public Object invoke(Object object, Method method, Object[] paramHolders) throws Throwable {
        return reloadInvoke(method, paramHolders);
    }

    /**
     * invoke handler which is triggered
     * by every method call which takes care of the reload
     *
     * @param method       the method to call
     * @param paramHolders the params
     * @return the return value of the operation
     * @throws InstantiationException    standard throw caused by reflection
     * @throws IllegalAccessException    standard throw caused by reflection
     * @throws java.lang.reflect.InvocationTargetException standard throw caused by reflection
     */

    protected Object reloadInvoke(Method method, Object[] paramHolders) throws InstantiationException, IllegalAccessException, InvocationTargetException {


        if (_delegate == null) {
            //stateless or lost state due to a lifecycle iteration we trigger anew
            _delegate = (WeavingContext.getInstance().reload(_loadedClass)).newInstance();
        } else {
            //if we are stateful only a tainted artifact is reloaded
            _delegate = WeavingContext.getInstance().reload(_delegate, _artifactType);

            //we work our way through all proxies and fetch the class for further reference
            Object delegate = WeavingContext.getDelegateFromProxy(_delegate);
            _loadedClass = delegate.getClass();
        }
        //check for proxies and unproxy them before calling the methods
        //to avoid unnecessary cast problems
        //this is slow on long param lists but it is better
        //to be slow than to have casts an calls in the code
        //for production we can compile the classes anyway and avoid
        //this
        unmapProxies(paramHolders);
        return method.invoke(_delegate, paramHolders);
    }

    /**
     * unmap proxied objects
     *
     * @param objects the objects to be unmapped
     */
    private void unmapProxies(Object[] objects) {
        if (objects == null) return;
        for (int cnt = 0; cnt < objects.length; cnt++) {
            objects[cnt] = WeavingContext.getDelegateFromProxy(objects[cnt]);
        }
    }

    public int getArtifactType() {
        return _artifactType;
    }

    public void setArtifactType(int artifactType) {
        _artifactType = artifactType;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

}
