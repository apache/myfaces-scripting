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
package org.apache.myfaces.extensions.scripting.servlet;

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.extensions.scripting.refresh.RefreshContext;
import org.apache.myfaces.webapp.StartupListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Note, since MyFaces 1.2.8 we have a startup and shutdown event system
 * which allows us to to hook event listener on servlet level before JSF is initialized
 * and after it is destroyed (and of course in the phases in between)
 * <p/>
 * We use this to start our scripting engine and to hook in our class loading
 * facilities before MyFaces performs its startup routines.
 *
 * @author Werner Punz
 */
public class StartupServletContextPluginChainLoader implements StartupListener {
    final Logger _log = Logger.getLogger(this.getClass().getName());

    public void preInit(ServletContextEvent servletContextEvent) {

        _log.info("[EXT-SCRIPTING] Instantiating StartupServletContextPluginChainLoader");

        ServletContext servletContext = servletContextEvent.getServletContext();
        if (servletContext == null) return;

        servletContext.setAttribute(ScriptingConst.CTX_ATTR_REQUEST_CNT, new AtomicInteger(0));
        servletContext.setAttribute(ScriptingConst.CTX_ATTR_STARTUP, new AtomicBoolean(Boolean.TRUE));

        initContext(servletContext);
        initChainLoader(servletContext);
        initCompileAndScan();
    }

    private void initCompileAndScan() {
        if (WeavingContext.isScriptingEnabled()) {
            _log.info("[EXT-SCRIPTING] Compiling all sources for the first time");
            WeavingContext.getWeaver().postStartupActions();
        }
    }

    /**
     * initializes our custom chain loader which gets plugged into
     * the MyFaces loading part for classes!
     *
     * @param servletContext the servlet context to be passed down
     * @return the custom chain loader for loading our classes over our classloaders
     */
    private CustomChainLoader initChainLoader(ServletContext servletContext) {
        CustomChainLoader loader = new CustomChainLoader(servletContext);
        ClassUtils.addClassLoadingExtension(loader, true);
        return loader;
    }

    /**
     * initializes the central config storage!
     *
     * @param servletContext the applications servlet context
     */
    private void initContext(ServletContext servletContext) {
        WeavingContext.startup(servletContext);
    }

    public void postInit(ServletContextEvent evt) {
        //tell the system that the startup phase is done
        evt.getServletContext().setAttribute(ScriptingConst.CTX_ATTR_STARTUP, new AtomicBoolean(Boolean.FALSE));
    }

    public void preDestroy(ServletContextEvent evt) {

    }

    public void postDestroy(ServletContextEvent evt) {
        //context is destroyed we have to shut down our daemon as well, by giving it
        //a hint to shutdown

        //TODO this is probably not needed because we run in a daemon thread anyway
        //so the servlet should not have a problem to shut it down externally
        RefreshContext rContext = (RefreshContext) evt.getServletContext().getAttribute("RefreshContext");
        rContext.getDaemon().setRunning(false);
    }

}