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
package org.apache.myfaces.extensions.scripting.core.servlet;

import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This filter is just a blocking filter
 * refreshes are not allowed while the system
 * recompiles, incoming requests are put on hold
 *
 * @author Werner Punz
 */
@WebFilter(urlPatterns = {"/*"},
        dispatcherTypes = {DispatcherType.REQUEST,
                           DispatcherType.FORWARD,
                           DispatcherType.INCLUDE,
                           DispatcherType.ERROR})
public class ScriptingServletFilter implements Filter
{

    ServletContext _context;
    Logger logger = Logger.getLogger(this.getClass().getName());

    public void init(FilterConfig filterConfig) throws ServletException
    {
        _context = filterConfig.getServletContext();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        synchronized (WeavingContext.getInstance().recompileLock)
        {
            logger.fine("request");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy()
    {
    }

}