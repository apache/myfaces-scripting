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

import org.apache.myfaces.extensions.scripting.cdi.loaders.ClassReloadingListener;
import org.apache.myfaces.extensions.scripting.cdi.loaders.support.ThrowAwayClassLoader;
import org.apache.webbeans.component.AbstractOwbBean;
import org.apache.webbeans.component.ManagedBean;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.util.WebBeansAnnotatedTypeUtil;
import org.apache.webbeans.util.WebBeansUtil;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
public class OpenWebBeansReloadingListener implements ClassReloadingListener {

    /** The logger instance for this class. */
    private static final Logger logger = Logger.getLogger(
            OpenWebBeansReloadingListener.class.getName());

    private static Field PASSIVATING_ID;
    static {
        try {
            PASSIVATING_ID = AbstractOwbBean.class.getDeclaredField("passivatingId");
            PASSIVATING_ID.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            PASSIVATING_ID = null;

            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Could not find the field 'passivatingId'.", ex);
            }
        }
    }

    private AtomicInteger passivatingIdCounter = new AtomicInteger();

    // ------------------------------------------ ClassReloadingListener methods

    @Override
    public void classReloaded(ThrowAwayClassLoader oldClassLoader, ThrowAwayClassLoader newClassLoader,
                                String className) {
        BeanManagerImpl beanManager = BeanManagerImpl.getManager();
        for (Bean<?> bean : new HashSet<Bean<?>>(beanManager.getBeans())) {
            if (bean.getBeanClass().getName().equals(className)) {
                // Reset the class to use.
                try {
                    // Replace the bean to use.
                    if (bean instanceof ManagedBean) {
                        beanManager.getBeans().remove(bean);
                        if (bean.getName() != null) {
                            ManagedBean<?> newBean = WebBeansAnnotatedTypeUtil.defineManagedBean(
                                    beanManager.createAnnotatedType(
                                            newClassLoader.loadClass(className, true)));
                            String passivatingId = newBean.getId();
                            try {
                                PASSIVATING_ID.set(newBean, passivatingId + "#" + passivatingIdCounter.incrementAndGet());
                            } catch (IllegalAccessException ex) {
                                throw new IllegalStateException(
                                        "Could not reset the passivation id of the bean " + newBean + ".", ex);
                            }

                            beanManager.addBean(newBean);

                            // Clear the caches of the injection resolver so that it considers these new beans.
                            if (beanManager instanceof ReloadingBeanManager) {
                                ReloadingBeanManager reloadingBeanManager = (ReloadingBeanManager) beanManager;
                                reloadingBeanManager.clearCaches();
                            } else {
                                logger.warning("Could not clear the bean manager's caches.");
                            }

                            if (logger.isLoggable(Level.INFO)) {
                                logger.info("Replaced the bean class of the bean '" + bean.getName() + "'.");
                            }
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    logger.log(Level.SEVERE,
                            "Could not replace bean definition.", ex);
                }
            }
        }
    }
    
}
