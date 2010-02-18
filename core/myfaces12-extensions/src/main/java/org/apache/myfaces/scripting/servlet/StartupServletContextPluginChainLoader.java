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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.myfaces.scripting.api.Configuration;
import org.apache.myfaces.scripting.core.util.Strategy;
import org.apache.myfaces.webapp.StartupListener;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.refresh.RefreshContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author werpu
 *         <p/>
 *         <p/>
 *         Startup context plugin chainloader
 *         for MyFaces 1.2.x,
 *         we hook ourselves into the startup event
 *         system we have for MyFaces 1.2.x+ to do the initial
 *         configuration before the MyFaces init itself starts!
 */
public class StartupServletContextPluginChainLoader implements StartupListener {
    final Log log = LogFactory.getLog(this.getClass());

    public void preInit(ServletContextEvent servletContextEvent) {

        log.info("[EXT-SCRIPTING] Instantiating StartupServletContextPluginChainLoader");

        ServletContext servletContext = servletContextEvent.getServletContext();
        if (servletContext == null) return;

        servletContext.setAttribute(ScriptingConst.CTX_REQUEST_CNT, new AtomicInteger(0));
        servletContext.setAttribute(ScriptingConst.CTX_STARTUP, new AtomicBoolean(Boolean.TRUE));


        initConfig(servletContext);
        CustomChainLoader loader = initChainLoader(servletContext);
        ScriptingWeaver weaver = initScriptingWeaver(servletContext, loader);
        initRefreshContext(servletContext);

        initInitialCompileAndScan(weaver);
    }

    /**
     * initiates the first compile and scan in the subsystem
     *
     * @param weaver our weaver which receives the trigger calls
     */
    private void initInitialCompileAndScan(ScriptingWeaver weaver) {
        log.info("[EXT-SCRIPTING] Compiling all sources for the first time");
        weaver.initiateStartup();
    }

    /**
     * initialisation of the refresh context object
     * the refresh context, is a context object which keeps
     * the refresh information (refresh time, needs refresh) etc...
     *
     * @param servletContext the servlet context singleton which keeps
     *                       the context for distribution
     */
    private void initRefreshContext(ServletContext servletContext) {
        RefreshContext rContext = new RefreshContext();
        servletContext.setAttribute("RefreshContext", rContext);
        rContext.getDaemon().initWeavingContext(servletContext);
        WeavingContext.setRefreshContext(rContext);
    }

    /**
     * The initialisation of our global weaver chain
     * which triggers the various subweavers depending
     * on the scripting engine plugged in.
     *
     * @param servletContext the application scoped holder for our weaver
     * @param loader         the chain loader which serves the weavers
     * @return the weaver instance which is generated and stored
     */
    private ScriptingWeaver initScriptingWeaver(ServletContext servletContext, CustomChainLoader loader) {
        ScriptingWeaver weaver = loader.getScriptingWeaver();
        servletContext.setAttribute("ScriptingWeaver", weaver);
        return weaver;
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
    private void initConfig(ServletContext servletContext) {
        final Configuration conf = new Configuration();
        servletContext.setAttribute(ScriptingConst.CTX_CONFIGURATION, conf);
        WeavingContext.setConfiguration(conf);
        //we now add the resource loader path here

        /*
         * we define a set of closures (inner classes) which make
         * our code more reusable we define a strategy
         * for each comma delimited set of values
         */
        Strategy addResourceDirStrategy = new Strategy() {
            public void apply(Object element) {
                conf.addResourceDir((String) element);
            }
        };
        Strategy addAdditionalClassPathStrategy = new Strategy() {
            public void apply(Object element) {
                conf.addAdditionalClassPath((String) element);
            }
        };
        Strategy addWhiteListPackageStrategy = new Strategy() {
            public void apply(Object element) {
                conf.addWhitelistPackage((String) element);
            }
        };

        /**
         * We now apply the values into our own lists
         */
        applyEntries(servletContext.getInitParameter(ScriptingConst.INIT_PARAM_RESOURCE_PATH), addResourceDirStrategy);
        applyEntries(servletContext.getInitParameter(ScriptingConst.INIT_PARAM_SCRIPTING_ADDITIONAL_CLASSPATH), addAdditionalClassPathStrategy);
        applyEntries(servletContext.getInitParameter(ScriptingConst.INIT_PARAM_SCRIPTING_PACKAGE_WHITELIST), addWhiteListPackageStrategy);

    }

    private void applyEntries(String val, Strategy strategy) {
        if (!StringUtils.isBlank(val)) {
            String[] splittedVal = val.split(ScriptingConst.CONTEXT_VALUE_DIVIDER);
            for (String singleVal : splittedVal) {
                strategy.apply(singleVal);
            }
        }
    }

    public void postInit(ServletContextEvent evt) {
        evt.getServletContext().setAttribute(ScriptingConst.CTX_STARTUP, new AtomicBoolean(Boolean.FALSE));

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