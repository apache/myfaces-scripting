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
import org.apache.myfaces.scripting.jsf.ScriptingConst;


import javax.servlet.*;
import java.io.IOException;
import org.apache.myfaces.groovyloader.core.DelegatingGroovyClassloader;


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
         //
         Object groovyDynamicLoader = context.getAttribute("GroovyDynamicLoader");
         if(groovyDynamicLoader != null) {
             ProxyUtils.setWeaver(groovyDynamicLoader);
         } else {
             initClassloader();
         }

         filterChain.doFilter(servletRequest, servletResponse);
     }

     public void destroy() {
         ProxyUtils.clean();
     }

     /**
     * main classloader init we can kickstart it only once our context is initialized
     * we have to recheck because sometimes the myfaces init
     * might trigger before our init and the filter might get called
     * before we have our own context classloader in place
     */
     protected void initClassloader() {
         //TODO check whether we have to reinitialize only once
         if (context == null) return;
         ClassLoader cls = Thread.currentThread().getContextClassLoader();
         boolean found = false;
         do {
             found = cls instanceof DelegatingGroovyClassloader;
             cls = cls.getParent();
         } while (!found && cls != null);
         if (found) {
             return;  /*we skip this here because we already have the classloader in place*/
         }

         /**
          * We have to do that because many containers
          * exchange classloaders on the fly and
          * and sometimes our classloader simply is dropped
          */
     
         //TODO to eliminate the classloader handling we have to add
         //the static data for our reloader into the context
         //and then reinitialize the loader constantly

         //we have to make sure that our cleanup thread is not touched by this


         ClassLoader newLoader = (ClassLoader) context.getAttribute(ScriptingConst.SCRIPTING_CLASSLOADER);
         Thread.currentThread().setContextClassLoader(newLoader);
         //we have to add a remove here because some nio based containers try to serialize the context attributes
         
     }

}