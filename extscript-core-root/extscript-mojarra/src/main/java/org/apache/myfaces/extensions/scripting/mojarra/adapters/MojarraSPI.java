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

package org.apache.myfaces.extensions.scripting.mojarra.adapters;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.mgbean.BeanBuilder;
import com.sun.faces.mgbean.BeanManager;
import com.sun.faces.mgbean.ManagedBeanInfo;
import org.apache.myfaces.extensions.scripting.core.api.ImplementationService;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.monitor.ClassResource;
import org.apache.myfaces.extensions.scripting.mojarra.common.ClassLoaderUtils;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class MojarraSPI implements ImplementationService
{
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void registerClassloadingExtension(ServletContext context)
    {
        ClassLoaderUtils.registerThrowAwayClassloader();
    }

    @Override
    public Class forName(String clazz)
    {
        try
        {
            return ClassLoaderUtils.getDefaultClassLoader().loadClass(clazz);
        }
        catch (ClassNotFoundException e)
        {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    @Override
    public void refreshManagedBeans()
    {
        //see the myfaces implementation of this mechanism
        if (FacesContext.getCurrentInstance() == null)
        {
            return;//no npe allowed
        }

        Collection<ClassResource> tainted = WeavingContext.getInstance().getTaintedClasses();
        Set<String> taints = new HashSet<String>();
        for (ClassResource taintedClass : tainted)
        {
            if (taintedClass.getAClass() != null)
            {
                taints.add(taintedClass.getAClass().getName());
            }
        }

        if (taints.size() > 0)
        {
            //We now have to check if the tainted classes belong to the managed beans
            Set<String> managedBeanClasses = new HashSet<String>();
            ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            BeanManager manager = ApplicationAssociate.getInstance(context).getBeanManager();
            Map<String, BeanBuilder> registeredBeans = manager.getRegisteredBeans();
            List<ManagedBeanInfo> taintedBeans = new ArrayList<ManagedBeanInfo>(taints.size());
            for (Map.Entry<String, BeanBuilder> entry : registeredBeans.entrySet())
            {
                ManagedBeanInfo info = entry.getValue().getManagedBeanInfo();
                //bean found
                if (taints.contains(info.getClassName()))
                {
                    taintedBeans.add(info);
                }
            }

            for(ManagedBeanInfo info : taintedBeans) {
               // String scope = info.getScope();
                //new interface which comes with mojarra 2.2.x
                manager.removeFromScope(info.getName(), FacesContext.getCurrentInstance());
            }

        }
    }

    //fix for mojarra

}
