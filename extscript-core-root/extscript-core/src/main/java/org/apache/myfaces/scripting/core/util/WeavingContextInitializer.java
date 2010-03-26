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

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.scripting.api.Configuration;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.CoreWeaver;
import org.apache.myfaces.scripting.loaders.groovy.GroovyScriptingWeaver;
import org.apache.myfaces.scripting.loaders.java.JavaScriptingWeaver;
import org.apache.myfaces.scripting.refresh.RefreshContext;
import org.apache.myfaces.scripting.servlet.ScriptingServletFilter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Moved over from Weblets...
 *          a fully functional web.xml parser
 *          to allow early access to the configuration
 */

class WeavingContextInitializer {

    static final Logger _logger = Logger.getLogger(WeavingContextInitializer.class.getName());



    public static void initWeavingContext(ServletContext servletContext) {
       
        validateWebXml(servletContext);
        initConfiguration(servletContext);
        initWeavers(servletContext);
        initRefreshContext(servletContext);
    }

    private static void initConfiguration(ServletContext servletContext) {
        final Configuration conf = new Configuration();
        servletContext.setAttribute(ScriptingConst.CTX_ATTR_CONFIGURATION, conf);
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

    private static void applyEntries(String val, Strategy strategy) {
        if (!StringUtils.isBlank(val)) {
            String[] splittedVal = val.split(ScriptingConst.CONTEXT_VALUE_DIVIDER);
            for (String singleVal : splittedVal) {
                strategy.apply(singleVal);
            }
        }
    }


    private static void validateWebXml(ServletContext context) {
        try {
            URL webXml = context.getResource("/WEB-INF/web.xml");

            if (webXml != null) {
                InputStream in = webXml.openStream();
                try {
                    WebXmlParserImpl parser = new WebXmlParserImpl();
                    Digester digester = new Digester();
                    digester.setValidating(false);
                    digester.setEntityResolver(DisconnectedEntityResolver.sharedInstance());
                    digester.push(parser);
                    //We only check for the servlet filter
                    //the rest is already delivered by our context
                    digester.addCallMethod("web-app/servlet-filter", "addServletFilter", 2);
                    //digester.addCallMethod("web-app/filter-mapping/filter-name", "addFilterName", 2);
                    digester.parse(in);
                    //we can handle the rest of the configuration in a more secure manner
                } catch (SAXException e) {
                    _logger.severe("[EXT-SCRIPTING] Web.xml could not be parsed disabling scripting");
                    WeavingContext.setScriptingEnabled(false);

                } finally {
                    in.close();
                }
            }

        } catch (IOException e) {
            _logger.severe("[EXT-SCRIPTING] Web.xml could not be parsed disabling scripting");
            WeavingContext.setScriptingEnabled(false);

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
        rContext.getDaemon().initWeavingContext(servletContext);
        WeavingContext.setRefreshContext(rContext);
    }

    private static void setupScriptingPaths(ServletContext servletContext, ScriptingWeaver weaver, String contextRootKey, String initParams) {
        if (!WeavingContext.isScriptingEnabled()) {
            return;
        }

        String classRoot = "";
        String scriptingRoot = "";

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

    /**
     * DisconnectedEntityResolver prevents external network access during parsing in case the remote host cannot be reached.
     */
    private static class DisconnectedEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) {
            // use an empty input source
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }

        // no instances

        private DisconnectedEntityResolver() {
        }

        static public DisconnectedEntityResolver sharedInstance() {
            return _INSTANCE;
        }

        static private DisconnectedEntityResolver _INSTANCE = new DisconnectedEntityResolver();
    }

    private static  class WebXmlParserImpl {

        private void addServletFilter(String filterName, String filterClass) {
            if (filterName.equals("scriptingFilter") && filterClass.equals(ScriptingServletFilter.class.getName())) {
                WeavingContext.setScriptingEnabled(true);
            }
        }

    }

}
