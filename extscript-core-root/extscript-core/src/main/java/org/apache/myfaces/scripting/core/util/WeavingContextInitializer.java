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

import org.apache.myfaces.scripting.api.Configuration;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.CoreWeaver;
import org.apache.myfaces.scripting.core.util.stax.FilterClassDigester;
import org.apache.myfaces.scripting.loaders.groovy.GroovyScriptingWeaver;
import org.apache.myfaces.scripting.loaders.java.JavaScriptingWeaver;
import org.apache.myfaces.scripting.refresh.FileChangedDaemon;
import org.apache.myfaces.scripting.refresh.RefreshContext;
import org.apache.myfaces.scripting.servlet.ScriptingServletFilter;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Central initializer class for our
 * WeavingContext which does some semantic checking of the web.xml
 * and initializes everything in proper order
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class WeavingContextInitializer {

    static final Logger _logger = Logger.getLogger(WeavingContextInitializer.class.getName());

    public static void initWeavingContext(ServletContext servletContext) {

        validateWebXml(servletContext);
        initConfiguration(servletContext);
        initWeavers(servletContext);
        initRefreshContext(servletContext);
        initFileChangeDaemon(servletContext);
        WeavingContext.setExternalContext(servletContext);
    }

    private static void initFileChangeDaemon(ServletContext servletContext) {
        FileChangedDaemon.startup(servletContext);
        WeavingContext.getRefreshContext().setDaemon(FileChangedDaemon.getInstance());
    }

    private static void initConfiguration(ServletContext servletContext) {
        final Configuration configuration = new Configuration();
        servletContext.setAttribute(ScriptingConst.CTX_ATTR_CONFIGURATION, configuration);
        WeavingContext.setConfiguration(configuration);
        //we now add the resource loader path here

        /*
         * we define a set of closures (inner classes) which make
         * our code more reusable we define a strategy
         * for each comma delimited set of values
         */
        Strategy addResourceDirStrategy = new Strategy() {
            public void apply(Object element) {
                configuration.addResourceDir((String) element);
            }
        };
        Strategy addAdditionalClassPathStrategy = new Strategy() {
            public void apply(Object element) {
                configuration.addAdditionalClassPath((String) element);
            }
        };
        Strategy addWhiteListPackageStrategy = new Strategy() {
            public void apply(Object element) {
                configuration.addWhitelistPackage((String) element);
            }
        };

        /**
         * We now apply the values into our own lists
         */
        applyEntries(servletContext.getInitParameter(ScriptingConst.INIT_PARAM_RESOURCE_PATH), addResourceDirStrategy);
        applyEntries(servletContext.getInitParameter(ScriptingConst.INIT_PARAM_SCRIPTING_ADDITIONAL_CLASSPATH), addAdditionalClassPathStrategy);
        applyEntries(servletContext.getInitParameter(ScriptingConst.INIT_PARAM_SCRIPTING_PACKAGE_WHITELIST), addWhiteListPackageStrategy);

    }

    private static void applyEntries(String val, Strategy strategy) {
        if (!StringUtils.isBlank(val)) {
            String[] splitVal = val.split(ScriptingConst.CONTEXT_VALUE_DIVIDER);
            for (String singleVal : splitVal) {
                strategy.apply(singleVal);
            }
        }
    }

    private static void validateWebXml(ServletContext context) {
        try {
            URL webXml = context.getResource("/WEB-INF/web.xml");

            if (webXml != null) {
                WeavingContext.setScriptingEnabled(FilterClassDigester.findFilter(webXml, ScriptingServletFilter.class));
            }

        } catch (IOException e) {
            _logger.severe("[EXT-SCRIPTING] Web.xml could not be parsed disabling scripting");
            WeavingContext.setScriptingEnabled(false);
        }

        if (!WeavingContext.isScriptingEnabled()) {
            String warnMsg = "[EXT-SCRIPTING] The servlet filter has not been set, please check your web.xml for following entries:" +
                    "\n    <filter>\n" +
                    "        <filter-name>scriptingFilter</filter-name>\n" +
                    "        <filter-class>org.apache.myfaces.scripting.servlet.ScriptingServletFilter</filter-class>\n" +
                    "    </filter>\n" +
                    "    <filter-mapping>\n" +
                    "        <filter-name>scriptingFilter</filter-name>\n" +
                    "        <url-pattern>/*</url-pattern>\n" +
                    "        <dispatcher>REQUEST</dispatcher>\n" +
                    "        <dispatcher>FORWARD</dispatcher>\n" +
                    "        <dispatcher>INCLUDE</dispatcher>\n" +
                    "        <dispatcher>ERROR</dispatcher>\n" +
                    "    </filter-mapping>";
            _logger.severe(warnMsg);
        }
    }

    private static boolean initWeavers(ServletContext servletContext) {
        _logger.fine("[EXT-SCRIPTING] initializing the weaving contexts");

        ScriptingWeaver groovyWeaver = new GroovyScriptingWeaver(servletContext);
        ScriptingWeaver javaWeaver = new JavaScriptingWeaver(servletContext);

        setupScriptingPaths(servletContext, groovyWeaver, ScriptingConst.GROOVY_SOURCE_ROOT, ScriptingConst.INIT_PARAM_CUSTOM_GROOVY_LOADER_PATHS);
        setupScriptingPaths(servletContext, javaWeaver, ScriptingConst.JAVA_SOURCE_ROOT, ScriptingConst.INIT_PARAM_CUSTOM_JAVA_LOADER_PATHS);
        if (!WeavingContext.isScriptingEnabled()) {
            return true;
        }

        //we have to store it because our filter
        //does not trigger upon initialisation
        WeavingContext.setWeaver(new CoreWeaver(groovyWeaver, javaWeaver));
        servletContext.setAttribute(ScriptingConst.CTX_ATTR_SCRIPTING_WEAVER, WeavingContext.getWeaver());
        return false;
    }

    /**
     * initialisation of the refresh context object
     * the refresh context, is a context object which keeps
     * the refresh information (refresh time, needs refresh) etc...
     *
     * @param servletContext the servlet context singleton which keeps
     *                       the context for distribution
     */
    private static void initRefreshContext(ServletContext servletContext) {
        _logger.fine("[EXT-SCRIPTING] initializing the refresh context");

        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }
        RefreshContext rContext = new RefreshContext();
        servletContext.setAttribute(ScriptingConst.CTX_ATTR_REFRESH_CONTEXT, rContext);
        WeavingContext.setRefreshContext(rContext);
    }

    private static void setupScriptingPaths(ServletContext servletContext, ScriptingWeaver weaver, String contextRootKey, String initParams) {
        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }

        String classRoot = "";
        String scriptingRoot;

        String additionalLoaderPaths;

        String contextRoot = servletContext.getRealPath(contextRootKey);
        if (contextRoot == null) {
            _logger.warning("[EXT-SCRIPTING] one of the standard paths could not be resolved: " + contextRootKey + " this is either due to the path is missing or due to a configuration error! You can bypass the problem by setting additional loader paths if they are not set already!");
            contextRoot = "";

        }

        contextRoot = contextRoot.trim();
        scriptingRoot = contextRoot;

        additionalLoaderPaths = servletContext.getInitParameter(initParams);
        appendAdditionalPaths(additionalLoaderPaths, weaver);
        if (additionalLoaderPaths == null || additionalLoaderPaths.trim().equals("")) {
            if (contextRoot.equals("")) {

                _logger.warning("[EXT-SCRIPTING] Standard paths (WEB-INF/groovy and WEB-INF/java could not be determined, also no additional loader paths are set, I cannot start properly, please set additional loader paths for Ext-Scripting to work correctly!");
                _logger.warning("[EXT-SCRIPTING] I am disabling Ext-Scripting!");

                WeavingContext.setScriptingEnabled(false);
                return;
            }
            weaver.appendCustomScriptPath(scriptingRoot);
            weaver.appendCustomScriptPath(classRoot);
        }
    }

    private static void appendAdditionalPaths(String additionalLoaderPaths, ScriptingWeaver workWeaver) {
        if (!StringUtils.isBlank(additionalLoaderPaths)) {
            String[] additionalPaths = additionalLoaderPaths.split(",");
            for (String path : additionalPaths) {
                workWeaver.appendCustomScriptPath(path);
            }
        }
    }

}
