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
package org.apache.myfaces.scripting.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.config.FacesConfigValidator;
import org.apache.myfaces.config.FacesConfigurator;
import org.apache.myfaces.context.servlet.ServletExternalContextImpl;
import org.apache.myfaces.shared_impl.util.StateUtils;
import org.apache.myfaces.shared_impl.webapp.webxml.WebXml;
import org.apache.myfaces.groovyloader.core.DelegatingGroovyClassloader;

import javax.faces.context.ExternalContext;
import javax.faces.FactoryFinder;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Iterator;
import java.util.List;

/**
 * This is a hack to get the plugin into myfaces
 * in the long run we need an extension
 *
 * @author Werner Punz
 */
public class StartupServletContextListener
        implements ServletContextListener {
    private static final Log log = LogFactory.getLog(StartupServletContextListener.class);

    //TODO fix this we have to reference the old one to avoid constand double inits
    //instead if the new one
    static final String FACES_INIT_DONE
            = org.apache.myfaces.webapp.StartupServletContextListener.class.getName() + ".FACES_INIT_DONE";
    static final String GROOVY_FACES_INIT_DONE
            = StartupServletContextListener.class.getName() + ".GROOVY_FACES_INIT_DONE";

    public void contextInitialized(ServletContextEvent event) {
        initFaces(event.getServletContext());
    }


    //public static ClassLoader newLoader = null;


    public static void initFaces(ServletContext servletContext) {

        /**
         * we have to reinstantiate with a new classloader
         * to keep the factories intact
         */
        FactoryFinder.releaseFactories();


        initClassloader(servletContext);


        try {
            Boolean b = (Boolean) servletContext.getAttribute(FACES_INIT_DONE);
            Boolean g = (Boolean) servletContext.getAttribute(GROOVY_FACES_INIT_DONE);


            if (b == null || b.booleanValue() == false || g == null || g.booleanValue() == false) {
                log.trace("Initializing MyFaces with groovy extensions");

                //Load the configuration
                ExternalContext externalContext = new ServletExternalContextImpl(servletContext, null, null);

                //And configure everything
                new FacesConfigurator(externalContext).configure();


                if ("true".equals(servletContext
                        .getInitParameter(FacesConfigValidator.VALIDATE_CONTEXT_PARAM)) || "true".equals(servletContext
                        .getInitParameter(FacesConfigValidator.VALIDATE_CONTEXT_PARAM.toLowerCase()))) {
                    List list = FacesConfigValidator.validate(externalContext,
                            servletContext.getRealPath("/"));

                    Iterator iterator = list.iterator();

                    while (iterator.hasNext())
                        log.warn(iterator.next());

                }

                // parse web.xml
                WebXml.init(externalContext);

                //now we are done we have to set our method weaver
                //to get a good proxy handling on our stateless objects

                servletContext.setAttribute(FACES_INIT_DONE, Boolean.TRUE);
                servletContext.setAttribute(GROOVY_FACES_INIT_DONE, Boolean.TRUE);
            } else {
                log.info("MyFaces already initialized");
            }
        }
        catch (Exception ex) {
            log.error("Error initializing ServletContext", ex);
            ex.printStackTrace();
        }
        log.info("ServletContext '" + servletContext.getRealPath("/") + "' initialized.");

        if (servletContext.getInitParameter(StateUtils.INIT_SECRET) != null
                || servletContext.getInitParameter(StateUtils.INIT_SECRET.toLowerCase()) != null)
            StateUtils.initSecret(servletContext);

        //GroovyWeaver.servletContext;

    }

    private static void initClassloader(ServletContext servletContext) {
        if (!(Thread.currentThread().getContextClassLoader() instanceof DelegatingGroovyClassloader)) {
            ClassLoader newLoader = null;

            newLoader = new DelegatingGroovyClassloader(servletContext);
            Thread.currentThread().setContextClassLoader(newLoader);
            servletContext.setAttribute(ScriptingConst.SCRIPTING_CLASSLOADER, newLoader);
        }
    }


    public void contextDestroyed(ServletContextEvent e) {
        FactoryFinder.releaseFactories();
    }
}