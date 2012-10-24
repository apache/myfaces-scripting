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

package org.apache.myfaces.extensions.scripting.spring.context;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class CompilationAwareContextLoaderListener extends ContextLoaderListener
{

    public CompilationAwareContextLoaderListener()
    {
        super();
    }

    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        super.contextInitialized(event);
    }

    @Override
    public ContextLoader getContextLoader()
    {
        return super.getContextLoader();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event)
    {
        super.contextDestroyed(event);
    }

    /**
     * <p>Creates the context loader to use. Note that actually the context loader
     * starts up the web application context, this listener just delgates
     * to it.</p>
     *
     * @return the context loader to use to start up the web appplication context
     */
    @Override
    protected ContextLoader createContextLoader()
    {
        return new CompilationAwareContextLoader();
    }
}
