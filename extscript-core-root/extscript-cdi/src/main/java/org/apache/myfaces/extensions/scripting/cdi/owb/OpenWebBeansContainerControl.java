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

package org.apache.myfaces.extensions.scripting.cdi.owb;

import org.apache.myfaces.extensions.scripting.cdi.api.CdiContainer;
import org.apache.myfaces.extensions.scripting.cdi.api.ContextControl;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * OpenWebBeans specific implementation of CdiContainer
 */
public class OpenWebBeansContainerControl implements CdiContainer
{

    private ContainerLifecycle lifecycle;

    private ContextControl ctxCtrl = null;
    private Bean<ContextControl> ctxCtrlBean = null;
    private CreationalContext<ContextControl> ctxCtrlCreationalContext = null;

    @Override
    public  BeanManager getBeanManager()
    {
        return lifecycle.getBeanManager();
    }

    public synchronized void init() {
        lifecycle = WebBeansContext.getInstance().getService(ContainerLifecycle.class);
    }

    @Override
    public synchronized void boot(ServletContextEvent servletContext)
    {

        init();
        lifecycle.startApplication(servletContext);
    }

    @Override
    public synchronized void shutdown(ServletContextEvent servletContext)
    {
        if (ctxCtrl != null)
        {
            ctxCtrlBean.destroy(ctxCtrl, ctxCtrlCreationalContext);

        }

        if (lifecycle != null) 
        {
            lifecycle.stopApplication(servletContext);
        }
    }

    public synchronized ContextControl getContextControl(ServletContext context, HttpSession session)
    {
        if (ctxCtrl == null)
        {
            Set<Bean<?>> beans = getBeanManager().getBeans(ContextControl.class);
            ctxCtrlBean = (Bean<ContextControl>) getBeanManager().resolve(beans);
            ctxCtrlCreationalContext = getBeanManager().createCreationalContext(ctxCtrlBean);
            ctxCtrl = (ContextControl)
                    getBeanManager().getReference(ctxCtrlBean, ContextControl.class, ctxCtrlCreationalContext);
            ctxCtrl.setServletContext(context);
            ctxCtrl.setSession(session);
        }
        return ctxCtrl;
    }


}
