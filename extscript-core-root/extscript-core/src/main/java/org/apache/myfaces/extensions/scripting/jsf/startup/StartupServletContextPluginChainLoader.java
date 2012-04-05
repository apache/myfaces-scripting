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

package org.apache.myfaces.extensions.scripting.jsf.startup;

import org.apache.myfaces.extensions.scripting.core.api.Plugin;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.monitor.ResourceMonitor;
import org.apache.myfaces.extensions.scripting.jsf.adapters.MyFacesSPI;
import org.apache.myfaces.webapp.StartupListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          serves the startup process
 *          the chainloader hooks itself into the myfaces init process
 *          and is responsible for startup of the daemon the
 *          initial scan and compile
 */

public class StartupServletContextPluginChainLoader implements StartupListener
{
    static final Logger _log = Logger.getLogger(StartupServletContextPluginChainLoader.class.getName());
    Plugin[] _plugins = new Plugin[]{};

    public StartupServletContextPluginChainLoader()
    {
        ServiceLoader<Plugin> pluginLoader
                = ServiceLoader.load(Plugin.class);
        List<Plugin> plugins = new LinkedList<Plugin>();
        for (Plugin plugin : pluginLoader)
        {
            plugins.add(plugin);
        }
        _plugins = plugins.toArray(new Plugin[plugins.size()]);
    }

    public void preInit(ServletContextEvent servletContextEvent)
    {
        ServletContext servletContext = servletContextEvent.getServletContext();
        try
        {
            startup(servletContext);

            for (Plugin plugin : _plugins)
            {
                plugin.preInit(servletContextEvent);
            }
        }
        catch (IOException e)
        {
            _log.severe("[EXT-SCRIPTING] Engine startup failed terminating ext-scripting");
        }

    }

    public static void startup(ServletContext servletContext) throws IOException
    {
        if(ResourceMonitor.getInstance() != null && ResourceMonitor.getInstance().isAlive()) return;

        WeavingContext context = WeavingContext.getInstance();
        _log.info("[EXT-SCRIPTING] Instantiating StartupServletContextPluginChainLoader");
        context.initEngines();
        _log.info("[EXT-SCRIPTING] Loading configuration");
        context.getConfiguration().init(servletContext);
        _log.info("[EXT-SCRIPTING] Loading Scripting end");
        _log.info("[EXT-SCRIPTING] initializing startup daemon");
        ResourceMonitor.init(servletContext);
        _log.info("[EXT-SCRIPTING] initializing startup daemon end");
        _log.info("[EXT-SCRIPTING] Initial Scan and compile");
        //the initial scan should happen synchronsously
        ResourceMonitor.getInstance().performMonitoringTask();
        _log.info("[EXT-SCRIPTING] Starting Change Monitor");
        ResourceMonitor.getInstance().start();
        _log.info("[EXT-SCRIPTING] Startup done");
        _log.info("[EXT-SCRIPTING] init the chain loader for class loading");
        //TODO make this more generic depending on the implementation
        MyFacesSPI.getInstance().registerClassloadingExtension(servletContext);
        _log.info("[EXT-SCRIPTING] registering the JSF Implementation");
        WeavingContext.getInstance().setImplementation(MyFacesSPI.getInstance());
    }

    public void postInit(ServletContextEvent evt)
    {
        //tell the system that the startup phase is done
        WeavingContext.getInstance().markPostInit();
        for (Plugin plugin : _plugins)
        {
            plugin.postInit(evt);
        }
    }

    public void preDestroy(ServletContextEvent evt)
    {
        for (Plugin plugin : _plugins)
        {
            plugin.preDestroy(evt);
        }
    }

    public void postDestroy(ServletContextEvent evt)
    {
        //context is destroyed we have to shut down our daemon as well, by giving it
        //a hint to shutdown
        ResourceMonitor.getInstance().setRunning(false);
        for (Plugin plugin : _plugins)
        {
            plugin.postDestroy(evt);
        }
    }
}
