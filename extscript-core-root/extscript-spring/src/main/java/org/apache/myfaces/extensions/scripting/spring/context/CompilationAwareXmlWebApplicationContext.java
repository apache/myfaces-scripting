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

package org.apache.myfaces.extensions.scripting.spring.context;

import org.apache.myfaces.extensions.scripting.spring.bean.CompilationAwareRefreshableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @author Bernhard Huemer
 * @version $Revision$ $Date$
 */

public class CompilationAwareXmlWebApplicationContext extends XmlWebApplicationContext
{
    public CompilationAwareXmlWebApplicationContext()
    {
        super();
    }

    /**
     * Create an internal bean factory for this context.
     * Called for each {@link #refresh()} attempt.
     * <p>The default implementation creates a
     * {@link org.springframework.beans.factory.support.DefaultListableBeanFactory}
     * with the {@link #getInternalParentBeanFactory() internal bean factory} of this
     * context's parent as parent bean factory. Can be overridden in subclasses,
     * for example to customize DefaultListableBeanFactory's settings.
     *
     * @return the bean factory for this context
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowEagerClassLoading
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
     */
    protected DefaultListableBeanFactory createBeanFactory()
    {
        return new CompilationAwareRefreshableBeanFactory(getInternalParentBeanFactory());
    }
}
