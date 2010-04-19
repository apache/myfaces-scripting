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

import org.apache.myfaces.extensions.scripting.api.Configuration;
import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.api.ScriptingWeaver;
import org.apache.myfaces.extensions.scripting.core.CoreWeaver;
import org.apache.myfaces.extensions.scripting.core.util.stax.FilterClassDigester;
import org.apache.myfaces.extensions.scripting.loaders.groovy.GroovyScriptingWeaver;
import org.apache.myfaces.extensions.scripting.loaders.java.JavaScriptingWeaver;
import org.apache.myfaces.extensions.scripting.loaders.java.RecompiledClassLoader;
import org.apache.myfaces.extensions.scripting.refresh.FileChangedDaemon;
import org.apache.myfaces.extensions.scripting.refresh.RefreshContext;
import org.apache.myfaces.extensions.scripting.servlet.ScriptingServletFilter;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
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

    static final PrivilegedExceptionAction<RecompiledClassLoader> LOADER_ACTION = new PrivilegedExceptionAction<RecompiledClassLoader>() {
        public RecompiledClassLoader run() {
            return new RecompiledClassLoader(ClassUtils.getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_JAVA, ".java");
        }
    };
    private static final String GROOVY_NOT_FOUND = "[EXT-SCRIPTING] Groovy not found disabling Ext-Scripting Groovy support";
    private static final String GROOVY_OBJECT = "groovy.lang.GroovyObject";

    public static void initWeavingContext(ServletContext servletContext) {

        validateWebXml(servletContext);
        initConfiguration(servletContext);
        validateSecurityConstraints();
        initWeavers(servletContext);
        validateSourcePaths();
        initRefreshContext(servletContext);

        initFileChangeDaemon(servletContext);
        initExternalContext(servletContext);

    }

    /**
     * validates the source paths which were determined by the
     * startup for failures
     *
     */
    private static void validateSourcePaths() {
        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }
        Collection<String> dirs = WeavingContext.getConfiguration().getAllSourceDirs();
        for (String currentDir : dirs) {
            File probe = new File(currentDir);
            _logger.info("[PROBE]"+probe.getAbsolutePath());
            if (!probe.exists()) {
                _logger.log(Level.SEVERE, "[EXT-SCRIPTING] The directory {0} does not exist, disabling scripting support", probe);
                WeavingContext.setScriptingEnabled(false);
                return;
            }
        }
    }

    /**
     * asserts the security constraints
     * the only security which has to be allowed
     * is the creation of classloaders
     */
    private static void validateSecurityConstraints() {
        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }
        try {
            AccessController.doPrivileged(LOADER_ACTION);
        } catch (PrivilegedActionException e) {
            _logger.severe("[EXT-SCRIPTING] Class loader creation is prohibited by your security settings, I am going to disable Ext-Scripting");
            WeavingContext.setScriptingEnabled(false);
        }
    }

    private static void initExternalContext(ServletContext servletContext) {
        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }
        WeavingContext.setExternalContext(servletContext);
    }

    private static void initFileChangeDaemon(ServletContext servletContext) {
        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }

        FileChangedDaemon.startup(servletContext);
        WeavingContext.getRefreshContext().setDaemon(FileChangedDaemon.getInstance());
    }

    private static void initConfiguration(ServletContext servletContext) {
        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }
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
            String warnMsg = ScriptingConst.ERR_SERVLET_FILTER;
            _logger.severe(warnMsg);
        }
    }

    /**
     * inits the weaver chain which depends on the scripting
     * language supported by the internal jars
     *
     * @param servletContext the standard servlet context
     */
    private static void initWeavers(ServletContext servletContext) {
        _logger.fine("[EXT-SCRIPTING] initializing the weaving contexts");

        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }

        List<ScriptingWeaver> weavers = new ArrayList<ScriptingWeaver>(2);

        initGroovyWeaver(servletContext, weavers);
        initJavaWeaver(servletContext, weavers);
        if(WeavingContext.isFilterEnabled() && weavers.size() == 0) {
            _logger.info("[EXT-SCRIPTING] No scripting languages initialized disabling EXT-SCRIPTING ");
            WeavingContext.setScriptingEnabled(false);
            return;
        }

        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }

        WeavingContext.setWeaver(new CoreWeaver(weavers));
        servletContext.setAttribute(ScriptingConst.CTX_ATTR_SCRIPTING_WEAVER, WeavingContext.getWeaver());
    }

    /**
     * inits the standard java weaver
     * for weaving and recompiling java classes on the fly
     *
     * @param servletContext the standard servlet context
     * @param weavers        our list of weavers which should receive the resulting weaver
     */
    private static void initJavaWeaver(ServletContext servletContext, List<ScriptingWeaver> weavers) {
        ScriptingWeaver javaWeaver = new JavaScriptingWeaver(servletContext);
        setupScriptingPaths(servletContext, javaWeaver, ScriptingConst.JAVA_SOURCE_ROOT, ScriptingConst.INIT_PARAM_CUSTOM_JAVA_LOADER_PATHS);
        if(WeavingContext.getConfiguration().getSourceDirs(ScriptingConst.ENGINE_TYPE_JSF_JAVA).size() > 0) {
            weavers.add(javaWeaver);
        } else {
            _logger.log(Level.WARNING, "[EXT-SCRIPTING] No valid source path for Java found either add WEB-INF/java to your filesystem, or add a custom Java source path, disabling EXT-SCRIPTING Java support");
        }
    }

    /**
     * initializes our groovy weaver
     *
     * @param servletContext the servlet context
     * @param weavers        the list of weavers receiving the resulting weaver if an initialization is possoble
     */
    private static void initGroovyWeaver(ServletContext servletContext, List<ScriptingWeaver> weavers) {
        //check if groovy can be enabled:
        try {
            Class groovyObject = ClassUtils.forName(GROOVY_OBJECT);
            if (groovyObject != null) {
                //groovy found ewe now enabled our groovy weaving support
                ScriptingWeaver groovyWeaver = new GroovyScriptingWeaver(servletContext);
                setupScriptingPaths(servletContext, groovyWeaver, ScriptingConst.GROOVY_SOURCE_ROOT, ScriptingConst.INIT_PARAM_CUSTOM_GROOVY_LOADER_PATHS);
                if(WeavingContext.getConfiguration().getSourceDirs(ScriptingConst.ENGINE_TYPE_JSF_GROOVY).size() > 0) {
                    weavers.add(groovyWeaver);
                } else {
                    _logger.log(Level.WARNING, "[EXT-SCRIPTING] No valid source path for Groovy found either add WEB-INF/groovy to your filesystem, or add a custom Groovy source path, disabling EXT-SCRIPTING Groovy support");
                }
            }

        } catch (Exception e) {
            _logger.info(GROOVY_NOT_FOUND);
        }
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
            if (!StringUtils.isBlank(scriptingRoot)) {
                File probe = new File(scriptingRoot);
                if(probe.exists()) {
                    weaver.appendCustomScriptPath(scriptingRoot);

                } else {
                    _logger.log(Level.WARNING, "[EXT-SCRIPING] path {0} could not be found this might cause compile problems ", scriptingRoot);
                }
            }
            if (!StringUtils.isBlank(classRoot)) {
                weaver.appendCustomScriptPath(classRoot);
            }
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
