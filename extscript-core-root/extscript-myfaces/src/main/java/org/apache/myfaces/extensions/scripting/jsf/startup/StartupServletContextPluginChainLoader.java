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

import org.apache.myfaces.webapp.StartupListener;

import javax.servlet.ServletContextEvent;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class StartupServletContextPluginChainLoader implements StartupListener
{
    StartupServletContextPluginChainLoaderBase _delegate = null;

    public StartupServletContextPluginChainLoader()
    {
        _delegate = new StartupServletContextPluginChainLoaderBase();
    }

    @Override
    public void preInit(ServletContextEvent evt)
    {
        _delegate.preInit(evt);
    }

    @Override
    public void postInit(ServletContextEvent evt)
    {
        _delegate.postInit(evt);
    }

    @Override
    public void preDestroy(ServletContextEvent evt)
    {
        _delegate.preDestroy(evt);
    }

    @Override
    public void postDestroy(ServletContextEvent evt)
    {
        _delegate.postDestroy(evt);
    }
}
