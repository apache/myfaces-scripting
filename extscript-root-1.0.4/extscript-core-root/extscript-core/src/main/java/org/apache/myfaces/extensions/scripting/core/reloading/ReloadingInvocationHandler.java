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


import org.apache.myfaces.extensions.scripting.core.api.Decorated;
import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;

/**
 * <p/>
 * We set our own invocation handler
 * here to allow reflection utils directly targeting our
 * _delegate.
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@SuppressWarnings("unused")
public abstract class ReloadingInvocationHandler implements InvocationHandler, Decorated
{
    Class _loadedClass = null;
    Object _delegate = null;

    /**
     * simplified invoke for more dynamic upon invocation
     * on our reloading objects
     *
     * @param object    the object to be invoked on
     * @param method    the method to be invoked
     * @param arguments the arguments passed down
     * @return the return value of the operation
     */
    public Object invoke(Object object, String method, Object... arguments) {
        return ReflectUtil.executeMethod(object, method, arguments);
    }

    public Class getLoadedClass() {
        return _loadedClass;
    }

    public Object getDelegate() {
        return _delegate;
    }

    public void setDelegate(Object delegate) {
        _delegate = delegate;
    }

    public void setLoadedClassName(Class loadedClass) {
        this._loadedClass = loadedClass;
    }

}
