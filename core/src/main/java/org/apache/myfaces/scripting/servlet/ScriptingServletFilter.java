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

import org.apache.myfaces.scripting.core.util.ProxyUtils;
import org.apache.myfaces.scripting.api.ScriptingConst;


import javax.servlet.*;
import java.io.IOException;


/**
 * Scripting servlet filter
 *
 * @author Werner Punz
 */
public class ScriptingServletFilter implements Filter {

     ServletContext context = null;

     public void init(FilterConfig filterConfig) throws ServletException {
         //To change body of implemented methods use File | Settings | File Templates.
       //  if (context == null && filterConfig.getServletContext() instanceof StartupServletContextListener)
             context = filterConfig.getServletContext();
     }

     public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
         Object groovyDynamicLoader = context.getAttribute("MyFacesDynamicLoader");
         ProxyUtils.setWeaver(groovyDynamicLoader);
         filterChain.doFilter(servletRequest, servletResponse);
     }

     public void destroy() {
         ProxyUtils.clean();
     }

}