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

package org.apache.myfaces.extensions.scripting.cdi.core;

import org.apache.myfaces.extensions.scripting.cdi.api.CdiContainerLoader;
import org.apache.myfaces.extensions.scripting.cdi.owb.OpenWebBeansContainerControl;
import org.apache.myfaces.extensions.scripting.core.api.eventhandling.WeavingEvent;
import org.apache.myfaces.extensions.scripting.core.api.eventhandling.WeavingEventListener;
import org.apache.myfaces.extensions.scripting.core.api.eventhandling.events.RefreshBeginEvent;
import org.apache.myfaces.extensions.scripting.core.api.eventhandling.events.TaintedEvent;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p>&nbsp;</p>
 *          Reloading listener which triggers a container reload
 *          the reload can happen at the end of the extscript lifecycle
 *          when all beans have been tainted
 */
public class ReloadingListener implements WeavingEventListener
{
    boolean _tainted = false;
    ServletContext context;

    @Override
    public void onEvent(WeavingEvent evt)
    {
        if (evt instanceof TaintedEvent)
        {
            _tainted = true;
        } else if (evt instanceof RefreshBeginEvent)
        {
            RefreshBeginEvent refreshEvent = (RefreshBeginEvent) evt;
            if (_tainted)
            {
                _tainted = false;
                HttpServletRequest req = (HttpServletRequest) refreshEvent.getRequest();
                //TODO plug reloadable classloader in here temporarily
                //as context classloader, then restart the container
                //then restore the old classloader
                //ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
                //ClassLoader tempClassLoader = new ThrowAwayClassloader(oldClassLoader);
                //Thread.currentThread().setContextClassLoader(tempClassLoader);
                try
                {
                    OpenWebBeansContainerControl container = (OpenWebBeansContainerControl) CdiContainerLoader
                            .getCdiContainer();
                    container.init();

                    container.getContextControl(req.getServletContext(),
                            req.getSession()).stopContexts();
                    container.shutdown(new ServletContextEvent(req.getServletContext()));

                    CdiContainerLoader.getCdiContainer().boot(new ServletContextEvent(req.getServletContext()));

                    container.getContextControl(req.getServletContext(),
                            req.getSession()).startContexts();
                }
                finally
                {
                    //Thread.currentThread().setContextClassLoader(oldClassLoader);
                }
            }
        }
    }
}
