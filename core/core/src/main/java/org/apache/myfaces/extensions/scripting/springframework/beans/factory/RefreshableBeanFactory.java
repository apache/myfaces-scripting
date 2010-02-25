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
package org.apache.myfaces.extensions.scripting.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 *
 */
public interface RefreshableBeanFactory extends ConfigurableListableBeanFactory {

    /**
     * <p>Requests a refresh operation for the definition of the bean with the given name. However,
     * note that this doesn't necessarily mean that the bean definition gets refreshed immediately.
     * Calling this method just ensures that the bean factory uses a refreshed bean definition by
     * the next time you access the bean with the given name again.</p>
     *
     * @param name the name of the bean to refresh
     *
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if there is no bean with the given name
     * @throws org.springframework.beans.factory.BeanDefinitionStoreException  in case of an invalid bean definition
     */
    public void refreshBeanDefinition(String name) throws BeansException;

    /**
     * <p>Requests a refresh operation for definitions of beans with the given type. Again this
     * doesn't necessarily mean that those bean definitions get refreshed immediately as calling
     * this method just ensures that the bean factory uses a refreshed bean definition the next
     * time you access one of those beans again.</p>
     *
     * @param className the type that you want to refresh beans for
     * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check
     *
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if there is no bean with the given name
     * @throws org.springframework.beans.factory.BeanDefinitionStoreException  in case of an invalid bean definition
     */
    public void refreshBeanDefinitionsOfType(String className, boolean includeNonSingletons, boolean allowEagerInit)
                throws BeansException;

}
