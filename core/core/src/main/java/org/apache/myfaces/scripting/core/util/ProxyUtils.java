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
package org.apache.myfaces.scripting.core.util;

import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.api.DynamicClassIdentifier;
import org.apache.myfaces.scripting.api.BaseWeaver;
import org.apache.myfaces.scripting.core.MethodLevelReloadingHandler;
import org.apache.myfaces.scripting.core.DummyWeaver;
import org.apache.myfaces.scripting.refresh.FileChangedDaemon;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * A set of proxy utils called
 * by the various subsystems
 *
 * @author Werner Punz
 */
public class ProxyUtils {

    /**
     * we push our _weaver into the thread local
     * to avoid too many calls into the
     * context classloading hierarchy
     * this should speed things up a little bit.
     * <p/>
     * Note we could work with this with static
     * objects as well but since we also have to work with context
     * reloading we probably are safer with filters
     * a reference in the context and a threadLocal variable
     */
    static ThreadLocal _weaverHolder = new ThreadLocal();

    public static void init() {

    }

    public static void clean() {
        _weaverHolder.set(null);
    }

    public static void setWeaver(Object weaver) {
        _weaverHolder.set(weaver);
        if (FileChangedDaemon.getInstance().getWeavers() == null) {
            FileChangedDaemon.getInstance().setWeavers((ScriptingWeaver) weaver);
        }
    }

    public static boolean isScriptingEnabled() {
        return _weaverHolder.get() != null;
    }

    public static ScriptingWeaver getWeaver() {
        ScriptingWeaver weaver = (ScriptingWeaver) _weaverHolder.get();
        if (weaver == null) {
            Log log = LogFactory.getLog(ProxyUtils.class);
            log.warn("Scripting Weaver is not set. Disabling script reloading subsystem. Make sure you have the scripting servlet filter enabled in your web.xml");
            _weaverHolder.set(new DummyWeaver());
        }
        return (ScriptingWeaver) _weaverHolder.get();
    }

    /**
     * we create a proxy to an existing object
     * which does reloading of the internal class
     * on method level
     * <p/>
     * this works only on classes which implement contractual interfaces
     * it cannot work on things like the navigation handler
     * which rely on base classes
     *
     * @param o            the source object to be proxied
     * @param theInterface the proxying interface
     * @return a proxied reloading object of type theInterface
     */
    public static Object createMethodReloadingProxyFromObject(Object o, Class theInterface) {
        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                                      new Class[]{theInterface},
                                      new MethodLevelReloadingHandler(o));
    }

    /**
     * we create a proxy to an existing object
     * which does reloading of the internal class
     * on newInstance level
     *
     * @param o
     * @param theInterface
     * @return
     */
    public static Object createConstructorReloadingProxyFromObject(Object o, Class theInterface) {
        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                                      new Class[]{theInterface},
                                      new MethodLevelReloadingHandler(o));
    }


    /**
     * unmapping of a proxied object
     *
     * @param o the proxied object
     * @return the unproxied object
     */
    public static Object getDelegateFromProxy(Object o) {
        if (o instanceof Decorated)
            return ((Decorated) o).getDelegate();

        if (!Proxy.isProxyClass(o.getClass())) return o;
        InvocationHandler handler = Proxy.getInvocationHandler(o);
        if (handler instanceof Decorated) {
            return ((Decorated) handler).getDelegate();
        }
        return o;
    }


    public static boolean isDynamic(Class clazz) {
        return getWeaver().isDynamic(clazz);
    }

}
