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

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Scripting servlet filter
 * <p/>
 * hits the filter while the
 * init system is not entirely finished yet
 *
 * @author Werner Punz
 */
public class ScriptingServletFilter implements Filter {

    ServletContext _context = null;
    static volatile boolean _active = false;
    static volatile boolean _warned = false;

    public void init(FilterConfig filterConfig) throws ServletException {
        _context = filterConfig.getServletContext();
        /*we cannot use the context listener here
        * because we have the problem that we do not want to parse the web.xml*/
        WeavingContext.setFilterEnabled(true);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        assertInitialized();
        if (!_active) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        markRequestStart();
        WeavingContext.initThread(_context);
        WeavingContext.getRefreshContext().setCurrentlyRunningRequests(getRequestCnt());

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            markRequestEnd();
        }
    }

    /**
     * Checks for an initialized system and if not the filter will be deactivated
     * <p/>
     * the idea is to check the context in regular intervals
     * whether the startup process has been finished and then
     * allow the requests to pass through
     */
    private void assertInitialized() {
        if (_active) return;

        AtomicBoolean startup = (AtomicBoolean) _context.getAttribute(ScriptingConst.CTX_ATTR_STARTUP);
        if (startup == null) {
            if (!_warned) {
                Logger log = Logger.getLogger(ScriptingServletFilter.class.getName());
                log.warning("[EXT-SCRIPTING] the Startup plugin chainloader has not been set, ext scripting is not working" +
                        "please refer to the documentation for the org.apache.myfaces.FACES_INIT_PLUGINS parameter, deactivating servlet filter");
                _active = false;
            }
        }
        _active = !startup.get();
    }

    public void destroy() {

        WeavingContext.clean();
    }

    //we mark the request beginning and end for further synchronisation issues

    private final AtomicInteger getRequestCnt() {
        AtomicInteger retVal = (AtomicInteger) _context.getAttribute(ScriptingConst.CTX_ATTR_REQUEST_CNT);

        return retVal;
    }

    private int markRequestStart() {
        return getRequestCnt().incrementAndGet();
    }

    private int markRequestEnd() {
        return getRequestCnt().decrementAndGet();
    }

}