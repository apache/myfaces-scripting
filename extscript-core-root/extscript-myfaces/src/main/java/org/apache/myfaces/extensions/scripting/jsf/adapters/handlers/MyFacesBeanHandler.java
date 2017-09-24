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

package org.apache.myfaces.extensions.scripting.jsf.adapters.handlers;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 */

import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.annotation.LifecycleProvider;
import org.apache.myfaces.config.annotation.LifecycleProviderFactory;
import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;
import org.apache.myfaces.extensions.scripting.core.monitor.ClassResource;

import javax.faces.context.FacesContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean handler implementation
 * which encapsulates the myfaces specific parts
 * of the bean processing
 *
 *
 */
public class MyFacesBeanHandler
{

    static final Logger _logger = Logger.getLogger(MyFacesBeanHandler.class.getName());

    /**
     * constructor
     */
    public MyFacesBeanHandler()
    {

    }

    /**
     * Refreshes all managed beans
     * session, and personal scoped ones
     * <p>&nbsp;</p>
     * personal scoped beans are beans which
     * have either
     * <li> session scope </li>
     * <li> page scope </li>
     * <li> custom scope </li>
     */
    public void refreshAllManagedBeans()
    {
        if (FacesContext.getCurrentInstance() == null)
        {
            return;//no npe allowed
        }

        Collection<ClassResource> tainted = WeavingContext.getInstance().getTaintedClasses();
        Set<String> taints = new HashSet<String>();
        for (ClassResource taintedClass : tainted)
        {
            if(taintedClass.getAClass() != null) {
                taints.add(taintedClass.getAClass().getName());
            }
        }

        //scanElDependencies();

        if (taints.size() > 0)
        {
            //We now have to check if the tainted classes belong to the managed beans
            Set<String> managedBeanClasses = new HashSet<String>();

            Map mbeans = RuntimeConfig.getCurrentInstance(FacesContext.getCurrentInstance().getExternalContext()).getManagedBeans();
            Map mbeansSnapshotView;

            //synchronized (RefreshContext.BEAN_SYNC_MONITOR) {
            mbeansSnapshotView = makeSnapshot(mbeans);
            //}

            for (Object entry : mbeansSnapshotView.entrySet())
            {
                Object bean = (Object) ((Map.Entry) entry).getValue();

                managedBeanClasses.add((String) ReflectUtil.executeMethod(bean, "getManagedBeanClassName"));//bean.getManagedBeanClassName());
            }

            boolean managedBeanTainted = isAnyManagedBeanTainted(taints, managedBeanClasses);
            markPersonalScopeRefreshRecommended();
            getLog().info("[EXT-SCRIPTING] Tainting all beans to avoid classcast exceptions");
            if (managedBeanTainted)
            {
                globalManagedBeanRefresh(mbeansSnapshotView);
                //personalScopeRefresh();
            }
        }
    }


    /**
     * removes all bean references which have been tainted
     * (note for now we remove all dynamic references until we
     * get a more sophisticated handling of managed beans)
     *
     * @param workCopy the managed beam snapshot view
     */
    private void globalManagedBeanRefresh(Map workCopy)
    {
        Collection<ClassResource> tainted = WeavingContext.getInstance().getTaintedClasses();
        Set<String> taints = new HashSet<String>();
        for (ClassResource taintedClass : tainted)
        {
            if(taintedClass.getAClass() != null)
                taints.add(taintedClass.getAClass().getName());
        }

        for (Object entry : workCopy.entrySet())
        {
            Object bean = ((Map.Entry) entry).getValue();
            Class managedBeanClass = (Class) ReflectUtil.executeMethod(bean, "getManagedBeanClass");
            if (hasToBeRefreshed(taints, managedBeanClass))
            {
                //managed bean class found we drop the class from our session
                removeBeanReferences(bean);
            }
        }
    }

    /**
     * determines whether any bean in our managed bean list
     * is tainted or not
     *
     * @param tainted            a list of classes which are tainted in this iteration
     * @param managedBeanClasses a ist of classes which are our managed beans
     * @return true if one of the beans is tainted
     */
    private boolean isAnyManagedBeanTainted(Set<String> tainted, Set<String> managedBeanClasses)
    {
        boolean managedBeanTainted = false;
        for (String taintedClass : tainted)
        {
            if (managedBeanClasses.contains(taintedClass))
            {
                managedBeanTainted = true;
                break;
            }
        }
        return managedBeanTainted;
    }

    /**
     * removes the references from out static scope
     * for jsf2 we probably have some kind of notification mechanism
     * which notifies custom scopes
     *
     * @param bean the managed bean which all references have to be removed from
     */

    private void removeBeanReferences(Object bean)
    {
        String managedBeanName = (String) ReflectUtil.executeMethod(bean, "getManagedBeanName");

        if (getLog().isLoggable(Level.FINE))
        {
            getLog().log(Level.FINE, "[EXT-SCRIPTING] JavaScriptingWeaver.removeBeanReferences({0})", managedBeanName);
        }

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(managedBeanName);
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().remove(managedBeanName);
        removeCustomScopedBean(bean);
    }

    /**
     * @return the log for this class
     */
    protected Logger getLog()
    {
        return Logger.getLogger(this.getClass().getName());
    }

    /**
     * jsf2 helper to remove custom scoped beans
     *
     * @param bean the managed bean which has to be removed from the custom scope from
     */
    private void removeCustomScopedBean(Object bean)
    {
        Object scopeImpl = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ReflectUtil.executeMethod(bean, "getManagedBeanScope"));
        if (scopeImpl == null) return; //scope not implemented
        //we now have to revert to introspection here because scopes are a pure jsf2 construct
        //so we use a messaging pattern here to cope with it

        Object beanInstance = ReflectUtil.executeMethod(scopeImpl, "get", ReflectUtil.executeMethod(bean, "getManagedBeanName"));
        LifecycleProvider lifecycleProvider =
                LifecycleProviderFactory.getLifecycleProviderFactory().getLifecycleProvider(FacesContext.getCurrentInstance().getExternalContext());
        try
        {
            lifecycleProvider.destroyInstance(beanInstance);
        }
        catch (IllegalAccessException e)
        {
            _logger.log(Level.WARNING, "removeCustomScopedBean():", e);
        }
        catch (InvocationTargetException e)
        {
            _logger.log(Level.WARNING, "removeCustomScopedBean():", e);
        }
    }

    /**
     * MyFaces 2.0 keeps an immutable map over the session
     * and request scoped beans
     * if we alter that during our loop we get a concurrent modification exception
     * taking a snapshot in time fixes that
     *
     * @param mbeans the internal managed bean map which has to be investigated
     * @return a map with the class name as key and the managed bean info
     *         as value of the current state of the internal runtime config bean map
     */
    private Map makeSnapshot(Map mbeans)
    {
        Map workCopy;

        workCopy = new HashMap(mbeans.size());
        for (Object elem : mbeans.entrySet())
        {
            Map.Entry entry = (Map.Entry) elem;
            workCopy.put(entry.getKey(), entry.getValue());
        }

        return workCopy;
    }

    /**
     * sets the internal timer for other processes
     * to update their beans as well
     */
    private void markPersonalScopeRefreshRecommended()
    {
        long sessionRefreshTime = System.currentTimeMillis();
        // WeavingContext.getRefreshContext().setPersonalScopedBeanRefresh(sessionRefreshTime);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ScriptingConst.SESS_BEAN_REFRESH_TIMER, sessionRefreshTime);
    }

    /**
     * important, this method determines whether a managed bean class
     * has to be refreshed or not
     *
     * @param tainted          set of tainted classes
     * @param managedBeanClass the class to be checked for refresh criteria
     * @return true if the current bean class fulfills our refresh criteria
     */
    protected boolean hasToBeRefreshed(Set<String> tainted, Class managedBeanClass)
    {

        return WeavingContext.getInstance().isDynamic(managedBeanClass) && tainted.contains(managedBeanClass.getName());
    }

    public  void registerManagedBean(Class clazz, String beanName) {
        //TODO move this over from the beanimplementationListener
    }
    public  void removeManagedBean(String className) {
        //TODO move this over from the beanimplementationlistener
    }
        
}

