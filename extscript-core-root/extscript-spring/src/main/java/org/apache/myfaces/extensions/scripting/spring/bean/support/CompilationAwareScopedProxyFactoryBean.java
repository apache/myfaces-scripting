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

package org.apache.myfaces.extensions.scripting.spring.bean.support;

import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.scope.DefaultScopedObject;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.target.SimpleBeanTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Modifier;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @author Bernhard Huemer
 * @version $Revision$ $Date$
 */

public class CompilationAwareScopedProxyFactoryBean extends ProxyConfig implements FactoryBean, BeanFactoryAware
{

    /**
     * The TargetSource that manages scoping
     */
    private final SimpleBeanTargetSource scopedTargetSource = new SimpleBeanTargetSource();

    /**
     * The name of the target bean
     */
    private String targetBeanName;

    /**
     * The cached singleton proxy
     */
    private Object proxy;

    /**
     * Create a new ScopedProxyFactoryBean instance.
     */
    public CompilationAwareScopedProxyFactoryBean()
    {
        setProxyTargetClass(true);
    }

    /**
     * Set the name of the bean that is to be scoped.
     */
    public void setTargetBeanName(String targetBeanName)
    {
        this.targetBeanName = targetBeanName;
        this.scopedTargetSource.setTargetBeanName(targetBeanName);
    }

    public void setBeanFactory(BeanFactory beanFactory) {
          if (!(beanFactory instanceof ConfigurableBeanFactory)) {
              throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
          }
          ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;

          this.scopedTargetSource.setBeanFactory(beanFactory);

          ProxyFactory pf = new ProxyFactory();
          pf.copyFrom(this);
          pf.setTargetSource(this.scopedTargetSource);

          Class beanType = beanFactory.getType(this.targetBeanName);
          if (beanType == null) {
              throw new IllegalStateException("Cannot create scoped proxy for bean '" + this.targetBeanName +
                      "': Target type could not be determined at the time of proxy creation.");
          }
          if (!isProxyTargetClass() || beanType.isInterface() || Modifier.isPrivate(beanType.getModifiers())) {
              pf.setInterfaces(ClassUtils.getAllInterfacesForClass(beanType, cbf.getBeanClassLoader()));
          }

          // Add an introduction that implements only the methods on ScopedObject.
          ScopedObject scopedObject = new DefaultScopedObject(cbf, this.scopedTargetSource.getTargetBeanName());
          pf.addAdvice(new DelegatingIntroductionInterceptor(scopedObject));

          // Add the AopInfrastructureBean marker to indicate that the scoped proxy
          // itself is not subject to auto-proxying! Only its target bean is.
          pf.addInterface(AopInfrastructureBean.class);

          // Don't pass the classloader to the proxy factory, so that
          // CGLib will definitely create a new proxy.
          this.proxy = pf.getProxy();
      }


      public Object getObject() {
          if (this.proxy == null) {
              throw new FactoryBeanNotInitializedException();
          }
          return this.proxy;
      }

      public Class getObjectType() {
          if (this.proxy != null) {
              return this.proxy.getClass();
          }
          if (this.scopedTargetSource != null) {
              return this.scopedTargetSource.getTargetClass();
          }
          return null;
      }

      public boolean isSingleton() {
          return true;
      }

}
