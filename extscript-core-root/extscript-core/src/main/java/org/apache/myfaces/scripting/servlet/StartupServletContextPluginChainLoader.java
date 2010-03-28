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
package org.apache.myfaces.scripting.servlet;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.refresh.RefreshContext;
import org.apache.myfaces.webapp.StartupListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author werpu
 *         <p/>
 *         <p/>
 *         Startup context plugin chainloader
 *         for MyFaces
 *         we hook ourselves into the startup event
 *         system we have for MyFaces 1.2.x+ to do the initial
 *         configuration before the MyFaces init itself starts!
 */
public class StartupServletContextPluginChainLoader implements StartupListener {
    final Logger log = Logger.getLogger(this.getClass().getName());

    public void preInit(ServletContextEvent servletContextEvent) {

        log.info("[EXT-SCRIPTING] Instantiating StartupServletContextPluginChainLoader");

        ServletContext servletContext = servletContextEvent.getServletContext();
        if (servletContext == null) return;

        servletContext.setAttribute(ScriptingConst.CTX_ATTR_REQUEST_CNT, new AtomicInteger(0));
        servletContext.setAttribute(ScriptingConst.CTX_ATTR_STARTUP, new AtomicBoolean(Boolean.TRUE));

        initContext(servletContext);
        initChainLoader(servletContext);
        initStartup();
    }

    private void initStartup() {
        if (WeavingContext.isScriptingEnabled()) {
            log.info("[EXT-SCRIPTING] Compiling all sources for the first time");
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
        RefreshContext rContext = (RefreshContext) evt.getServletContext().getAttribute("RefreshContext");
        rContext.getDaemon().setRunning(false);
    }

}