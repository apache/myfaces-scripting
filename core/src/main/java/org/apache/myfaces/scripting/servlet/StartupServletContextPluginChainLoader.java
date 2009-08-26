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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.shared_impl.util.ClassUtils;
import org.apache.myfaces.webapp.StartupListener;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 * @author werpu
 * @date: 14.08.2009
 */
public class StartupServletContextPluginChainLoader implements StartupListener {


    public void preInit(ServletContextEvent servletContextEvent) {
        Log log = LogFactory.getLog(this.getClass());

        log.info("Instantiating StartupServletContextPluginChainLoader");

        ServletContext servletContext = servletContextEvent.getServletContext();
        if (servletContext == null) return;

        CustomChainLoader loader = new CustomChainLoader(servletContext);
        ClassUtils.addClassLoadingExtension(loader, true);
        servletContext.setAttribute("MyFacesDynamicLoader", loader.getScriptingWeaver());

   }

    public void postInit(ServletContextEvent evt) {
    }

    public void preDestroy(ServletContextEvent evt) {
    }

    public void postDestroy(ServletContextEvent evt) {
    }

}