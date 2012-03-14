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
package org.apache.myfaces.extensions.scripting.core.util;

import org.apache.myfaces.context.servlet.ApplicationMap;
import org.apache.myfaces.extensions.scripting.api.*;
import org.apache.myfaces.extensions.scripting.core.DummyWeaver;
import org.apache.myfaces.extensions.scripting.core.MethodLevelReloadingHandler;
import org.apache.myfaces.extensions.scripting.monitor.ResourceMonitor;
import org.apache.myfaces.extensions.scripting.monitor.RefreshContext;
import org.apache.myfaces.extensions.scripting.api.CompilationResult;
import org.apache.myfaces.extensions.scripting.api.extensionevents.ExtensionEventRegistry;
import org.apache.myfaces.extensions.scripting.jsf.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * A set of weaving context class called
 * by the various subsystems
 * <p/>
 * TODO (1.1) move this away from static methods into a singleton which is kept
 * in the application context, to keep the context pattern.
 *
 * @author Werner Punz
 */
public class WeavingContext {

    /**
     * <p>
     * we push our weaver into the thread local
     * to avoid too many calls into the
     * context class loading hierarchy
     * this should speed things up a little bit.
     * </p>
     * <p>
     * Note we could work with this with static
     * objects as well but since we also have to work with context
     * reloading we probably are safer with filters
     * a reference in the context and a threadLocal variable
     * </p>
     */
    static final protected ThreadLocal<Object> _weaverHolder = new ThreadLocal<Object>();
    static final protected ThreadLocal<Object> _refreshContextHolder = new ThreadLocal<Object>();
    static final protected ThreadLocal<Object> _configuration = new ThreadLocal<Object>();
    static final protected ThreadLocal<Object> _externalContext = new ThreadLocal<Object>();
    static final protected ThreadLocal<Object> _extensionEventSystem = new ThreadLocal<Object>();

    static final protected ThreadLocal<ServletRequest> _request = new ThreadLocal();
    static final protected ThreadLocal<Map<String, Object>> _requestMap = new ThreadLocal();

    private static final String WARN_WEAVER_NOT_SET = "[EXT-SCRIPTING] Scripting Weaver is not set. Disabling script reloading subsystem. Make sure you have the scripting servlet filter enabled in your web.xml";

    private static final Map<Integer, CompilationResult> _compilationResults = new ConcurrentHashMap<Integer, CompilationResult>();

    /**
     * per default the weaver is not set up
     */
    private static AtomicBoolean _enabled = new AtomicBoolean(false);

    /**
     * per default the weaver is not set up
     */
    private static AtomicBoolean _filterEnabled = new AtomicBoolean(false);

    /**
     * external helper which helps to initialize
     * the scripting engine runtime system
     * and to discover configuration mistakes early on
     *
     * @param servletContext the servlet context which holds the config data
     */
    public static void startup(ServletContext servletContext) {
        WeavingContextInitializer.initWeavingContext(servletContext);
    }

    public static void initThread(ServletContext context) {
        WeavingContext.setWeaver(context.getAttribute(ScriptingConst.CTX_ATTR_SCRIPTING_WEAVER));
        WeavingContext.setRefreshContext((RefreshContext) context.getAttribute(ScriptingConst.CTX_ATTR_REFRESH_CONTEXT));
        WeavingContext.setConfiguration((Configuration) context.getAttribute(ScriptingConst.CTX_ATTR_CONFIGURATION));
        WeavingContext.setExtensionEventRegistry((ExtensionEventRegistry) context.getAttribute(ScriptingConst.CTX_ATTR_EXTENSION_EVENT_SYSTEM));
        WeavingContext.setExternalContext(context);
    }


    public static CompilationResult getCompilationResult(Integer scriptingEngine) {
        return _compilationResults.get(scriptingEngine);
    }

    public static void setCompilationResult(Integer scriptingEngine, CompilationResult result) {
        _compilationResults.put(scriptingEngine, result);
    }

    public static void setExternalContext(Object context) {
        _externalContext.set(context);
    }

    public static Object getExternalContext() {
        return _externalContext.get();
    }

    /**
     * general shutdown clean
     */
    public static void clean() {
        _weaverHolder.set(null);
    }

    public static void setRefreshContext(RefreshContext rContext) {
        _refreshContextHolder.set(rContext);
    }

    public static RefreshContext getRefreshContext() {
        return (RefreshContext) _refreshContextHolder.get();
    }

    public static Configuration getConfiguration() {
        return (Configuration) _configuration.get();
    }

    public static void setConfiguration(Configuration configuration) {
        _configuration.set(configuration);
    }

    public static void setExtensionEventRegistry(ExtensionEventRegistry reg) {
        _extensionEventSystem.set(reg);
    }

    public static ExtensionEventRegistry getExtensionEventRegistry() {
        return (ExtensionEventRegistry) _extensionEventSystem.get();
    }

    public static void setRequest(ServletRequest req) {
        _request.set(req);
        _requestMap.set(new RequestMap(req));
    }

    public static ServletRequest getRequest() {
        return (ServletRequest) _request.get();
    }


    public static Map getRequestMap() {
        Map ret = (Map) _requestMap.get();
        if (ret == null) {
            //for startup we need a simulation
            _requestMap.set(new HashMap<String, Object>());
            ret = (Map) _requestMap.get();
        }
        return ret;
    }

    /**
     * the weavers are set from outside
     * we have to provide the weaver facade
     * for very thread accessing this system
     *
     * @param weaver the weaver object to be set from outside
     */
    public static void setWeaver(Object weaver) {
        _weaverHolder.set(weaver);

    }

    /**
     * some artefacts need a full request refresh
     */
    public static void doRequestRefreshes() {
        //TODO emit an application event here
        //which enforces attached libraries to refresh themselves
        if (isScriptingEnabled())
            getWeaver().requestRefresh();
    }

    public static void jsfRequestRefresh() {
        if (isScriptingEnabled())
            getWeaver().jsfRequestRefresh();
    }

    /**
     * checks whether the system
     * has scripting enabled or not
     *
     * @return true in case of being scriptable
     */
    public static boolean isScriptingEnabled() {
        return _enabled != null && _enabled.get();
    }

    public static void setScriptingEnabled(boolean enabled) {
        _enabled.set(enabled);
    }

    /**
     * the filter has to be treated differently
     * if the filter is not enabled we do not have
     * a chance to access our singletons properly
     * <p/>
     * The servlet api in 2.5 seems to lack a filter
     * accessor, so we have to set this from our filter and then
     * do periodic checks within our system
     *
     * @param enabled true set from out filter init
     */
    public static void setFilterEnabled(boolean enabled) {
        _filterEnabled.set(enabled);
    }

    /**
     * @return true if our filter is enabled
     */
    @SuppressWarnings("unused")
    public static boolean isFilterEnabled() {
        return _filterEnabled != null && _filterEnabled.get();
    }

    /**
     * fetches the weavers
     * for this current thread
     *
     * @return a ScriptingWeaver chain for all weavers currently supported
     */
    public static ScriptingWeaver getWeaver() {
        //shutting down condition _weaverHolder == null due to separate thread
        if (_weaverHolder == null) {
            return null;
        }
        ScriptingWeaver weaver = (ScriptingWeaver) _weaverHolder.get();
        if (weaver == null) {
            Logger log = Logger.getLogger(WeavingContext.class.getName());
            log.warning(WARN_WEAVER_NOT_SET);
            _weaverHolder.set(new DummyWeaver());
        }
        return weaver;
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
     * @param artifactType the artifact type to be reloaded
     * @return a proxied reloading object of type theInterface
     */
    public static Object createMethodReloadingProxyFromObject(Object o, Class theInterface, int artifactType) {
        if (!isScriptingEnabled()) {
            return o;
        }
        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                new Class[]{theInterface},
                new MethodLevelReloadingHandler(o, artifactType));
    }

    /**
     * we create a proxy to an existing object
     * which does reloading of the internal class
     * on newInstance level
     *
     * @param o            the original object
     * @param theInterface the proxy interface
     * @param artifactType the artifact type to be handled
     * @return the proxy of the object if scripting is enabled, the original one otherwise
     */
    @SuppressWarnings("unused")
    public static Object createConstructorReloadingProxyFromObject(Object o, Class theInterface, int artifactType) {
        if (!isScriptingEnabled()) {
            return o;
        }
        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                new Class[]{theInterface},
                new MethodLevelReloadingHandler(o, artifactType));
    }

    public static ResourceMonitor getFileChangedDaemon() {
        ResourceMonitor daemon = getRefreshContext().getDaemon();
        if (daemon.getWeavers() == null) {
            daemon.setWeavers(getWeaver());
        }
        return daemon;
    }

    /**
     * un-mapping of a proxied object
     *
     * @param o the proxied object
     * @return the un-proxied object
     */
    public static Object getDelegateFromProxy(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Decorated)
            return ((Decorated) o).getDelegate();

        if (!Proxy.isProxyClass(o.getClass())) return o;
        InvocationHandler handler = Proxy.getInvocationHandler(o);
        if (handler instanceof Decorated) {
            return ((Decorated) handler).getDelegate();
        }
        return o;
    }

    /**
     * checks if a class is dynamic
     *
     * @param clazz the class to be checked
     * @return true if the class is of
     *         dynamic nature and our scripting system is enabled
     */
    public static boolean isDynamic(Class clazz) {
        return isScriptingEnabled() && getWeaver().isDynamic(clazz);
    }

    public static AbstractThreadSafeAttributeMap<Object> getApplicationMap() {
        return new ServletApplicationMap((ServletContext) getExternalContext());
    }

}
