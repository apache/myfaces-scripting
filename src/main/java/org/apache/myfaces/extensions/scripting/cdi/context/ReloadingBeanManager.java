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

package org.apache.myfaces.extensions.scripting.cdi.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webbeans.component.AbstractOwbBean;
import org.apache.webbeans.config.WebBeansFinder;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.context.AbstractContext;
import org.apache.webbeans.util.WebBeansUtil;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ReloadingBeanManager extends BeanManagerImpl {

    /** The logger instance for this class. */
    private static final Logger logger = Logger.getLogger(
            OpenWebBeansReloadingListener.class.getName());

    private static Field CACHE_PROXIES_FIELD;
    static {
        try {
            CACHE_PROXIES_FIELD = BeanManagerImpl.class.getDeclaredField("cacheProxies");
            CACHE_PROXIES_FIELD.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            CACHE_PROXIES_FIELD = null;

            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Could not find the field 'cacheProxies'.", ex);
            }
        }
    }

    @Override
    public Set<Bean<?>> getBeans() {
        Set<Bean<?>> beans = super.getBeans();
        return beans;
    }

    @Override
    public Object getReference(Bean<?> bean, Type beanType, CreationalContext<?> creationalContext) {
        Object reference = super.getReference(bean, beanType, creationalContext);
        
        // If the reference refers to an outdated version of the bean, create a new one instead.
        if (!bean.getBeanClass().isInstance(reference)) {
            Context context = getContext(bean.getScope());
            if (context instanceof AbstractContext) {
                AbstractContext reloadableContext = (AbstractContext) context;
                if (reloadableContext.getComponentInstanceMap() != null) {
                    reloadableContext.getComponentInstanceMap().remove(bean);
                }

                Object newReference = super.getReference(bean, beanType, creationalContext);
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Removed the instance '" + reference + " from the context '" + context
                            + "' as it is outdated and replaced it with '" + newReference + "'.");   
                }

                return newReference;
            }
        }

        return reference;
    }

    public void clearCaches() {
        getInjectionResolver().clearCaches();

        if (CACHE_PROXIES_FIELD != null) {
            try {
                Map<?, ?> cacheProxies =
                        (Map<?, ?>) CACHE_PROXIES_FIELD.get(this);
                cacheProxies.clear();
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Could not clear the proxy caches of this bean manager.", ex);
            }
        }
    }

    public static void install() {
        try {
            Field singletonMapField = WebBeansFinder.class.getDeclaredField("singletonMap");
            singletonMapField.setAccessible(true);

            Map<ClassLoader, Map<String, Object>> singletonMap =
                    (Map<ClassLoader, Map<String, Object>>)
                            singletonMapField.get(null);
            Map<String, Object> singletons = singletonMap.get(WebBeansUtil.getCurrentClassLoader());
            if (singletons == null) {
                singletons = new HashMap<String, Object>();
                singletonMap.put(WebBeansUtil.getCurrentClassLoader(), singletons);
            }

            ReloadingBeanManager reloadingBeanManager = new ReloadingBeanManager();
            reloadingBeanManager.getBeans().addAll(
                    BeanManagerImpl.getManager().getBeans());
            
            singletons.put(BeanManagerImpl.class.getName(), reloadingBeanManager);
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Could not install a new reloading bean manager.", ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Could not install a new reloading bean manager.", ex);
        }
    }

}
