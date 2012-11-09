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

package org.apache.myfaces.extension.scripting.weld.startup;

import org.apache.commons.io.FileUtils;
import org.apache.myfaces.extension.scripting.weld.core.CDIThrowAwayClassloader;
import org.apache.myfaces.extension.scripting.weld.core.ReloadingListener;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.jsf.startup.StartupServletContextPluginChainLoaderBase;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          This class unfortunately has to be copied over from Weld with minor modifications
 *          the reason for it is that the bootstrap instance var is private final transient
 *          but we have to reroute it to our own bootstrapper.
 */

public class WeldBootstrapListener extends org.jboss.weld.environment.servlet.Listener
{

    public static final String RELOADING_LISTENER   = "ReloadingListener";
    public static final String WELD_CONTAINER       = "WeldContainer";
    public static final String WELD_BOOTSTRAP       = "Bootstrap";
    public static final String WELD_LISTENER        = "WeldListener";
    private static final String FIELD_CONTAINER = "container";
    private static final String FIELD_BOOTSTRAP = "bootstrap";
    private static final String FIELD_WELD_LISTENER1 = "weldListener";

    /**
     * we initialize ext-script here
     * in conjunction with the
     *
     * @param servletContext
     */
    private void initExtScript(ServletContext servletContext)
    {
        try
        {
            //the reloading listener also is the marker to avoid double initialisation
            //after the container is kickstarted
            if (servletContext.getAttribute(RELOADING_LISTENER) == null)
            {
                StartupServletContextPluginChainLoaderBase.startup(servletContext);
                servletContext.setAttribute(RELOADING_LISTENER, new ReloadingListener());
                WeavingContext.getInstance().addListener((ReloadingListener) servletContext.getAttribute(RELOADING_LISTENER));
                File target = WeavingContext.getInstance().getConfiguration().getCompileTarget("/META-INF/beans.xml");

                //we generate a beans.xml for weld, owb seems to be able to cope without it
                String beansXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<beans xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
                        "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "       xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">\n" +
                        "</beans>";
                FileUtils.writeStringToFile(target, beansXML);

            }
            //we have to set a classloader here, because otherwise the bean discovery
            //would fail, weld uses a different method than owb for the discovery

            Thread.currentThread().setContextClassLoader(new CDIThrowAwayClassloader(Thread.currentThread().getContextClassLoader()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //we initialize ext-scripting here  we replace the classloader
        //because weld scans the archives from a resource list
        initExtScript(sce.getServletContext());
        super.contextInitialized(sce);
        //now that weld is initialized we reset the classloader for myfaces init
        Thread.currentThread().setContextClassLoader(loader);
        //we now fetch the container and whatever we have
    }

}
