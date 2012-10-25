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

import org.apache.myfaces.extensions.scripting.core.engine.ThrowAwayClassloader;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.CglibSubclassingInstantiationStrategy;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Conventions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * <p>Object instantiation strategy that knows how deal with reloaded classes. The problem is
 * that Spring internally tries to cache the constructor it used to instantiate a bean. However,
 * if the Class gets reloaded, the instantiation strategy has to resolve the constructor to use
 * again as otherwise it would create an instance of an outdated class.</p>
 * <p/>
 * <p>Note that even though this class extends another class that seems to have a dependency on
 * CGLIB, this is not the case actually. Only if you're using method injection CGLIB has to be
 * available on the classpath.</p>
 * <p/>
 * <p>TODO: Invalidate argument caches.
 * Spring internally caches the arguments to use for instantiating a new bean, i.e. it caches
 * both the constructor / factory method to use and the according resolved arguments. However,
 * this will most probably cause problems.
 * </p>
 * @author Bernhard Huemer (latest modification by $Author$)
 */
public class CompilationAwareInstantiationStrategy extends CglibSubclassingInstantiationStrategy
{

    /**
     * The name of the attribute that contains the cached constructor to use.
     */
    private static final String CACHED_CONSTRUCTOR =
            Conventions.getQualifiedAttributeName(CompilationAwareInstantiationStrategy.class, "cachedConstructor");

    /**
     * The name of the attribute that contains the cached factory method to use.
     */
    private static final String CACHED_FACTORY_METHOD =
            Conventions.getQualifiedAttributeName(CompilationAwareInstantiationStrategy.class, "cachedFactoryMethod");

    /**
     * <p>Return an instance of the bean with the given name in this factory.</p>
     *
     * @param beanDefinition the bean definition
     * @param beanName       name of the bean when it's created in this context.
     *                       The name can be <code>null</code> if we're autowiring a bean that
     *                       doesn't belong to the factory.
     * @param owner          owning BeanFactory
     * @return a bean instance for this bean definition
     * @throws org.springframework.beans.BeansException
     *          if the instantiation failed
     */
    public Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) throws BeansException
    {
        // Determine whether the given bean definition supports a refresh operation,
        // i.e. if a refresh metadata attribute has been attached to it already.
        boolean refreshableAttribute = (beanDefinition.getBeanClass().getClassLoader() instanceof ThrowAwayClassloader);
        if (refreshableAttribute)
        {
            // At this point the bean factory has already re-resolved the bean class, so it's safe
            // to use it. We don't have to care about whether it's the most recent Class object
            // at this point anymore.
            Constructor constructorToUse = null;
            Class classObj = beanDefinition.getBeanClass();
            if (classObj.isInterface())
            {
                throw new BeanInstantiationException(classObj, "Specified class is an interface");
            } else
            {
                try
                {
                    constructorToUse = classObj.getDeclaredConstructor((Class[]) null);
                }
                catch (Exception ex)
                {
                    throw new BeanInstantiationException(classObj, "No default constructor found", ex);
                }
            }
            return BeanUtils.instantiateClass(constructorToUse, null);
        } else
        {
            return super.instantiate(beanDefinition, beanName, owner);
        }
    }

    /**
     * <p>Return an instance of the bean with the given name in this factory,
     * creating it via the given constructor. However, if the bean needs to be
     * refreshed (according to the refreshable meta attribute), the constructor
     * will be reloaded, i.e. it will be reevaluated using reflection given
     * the parameter types.</p>
     *
     * @param beanDefinition the bean definition
     * @param beanName       name of the bean when it's created in this context.
     *                       The name can be <code>null</code> if we're autowiring a bean
     *                       that doesn't belong to the factory.
     * @param owner          owning BeanFactory
     * @param ctor           the constructor to use
     * @param args           the constructor arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation failed
     */
    public Object instantiate(RootBeanDefinition beanDefinition, String beanName,
                              BeanFactory owner, Constructor ctor, Object[] args)
    {
        // The constructor which we'll use to instantiate the bean.
        Constructor constructorToUse = ctor;

        // Determine whether the given bean definition supports a refresh operation,
        // i.e. if a refresh metadata attribute has been attached to it already.
        boolean refreshableAttribute = (beanDefinition.getBeanClass().getClassLoader() instanceof ThrowAwayClassloader);
        if (refreshableAttribute)
        {
            //constructorToUse = (Constructor) beanDefinition.getAttribute(CACHED_CONSTRUCTOR);
            //if (constructorToUse == null || refreshableAttribute.requiresRefresh())
            //{
                try
                {
                    // Reload the constructor to use. The problem is that the given constructor references
                    // the outdated Class object, which means, that if we used the given constructor to
                    // instantiate another object, we would end up with an instance of the outdated class.
                    constructorToUse = beanDefinition.getBeanClass().getConstructor(ctor.getParameterTypes());

                    // Cache the constructor to use so that we don't have to use reflection every time.
                    //beanDefinition.setAttribute(CACHED_CONSTRUCTOR, constructorToUse);
                }
                catch (NoSuchMethodException ex)
                {
                    throw new BeanInstantiationException(
                            beanDefinition.getBeanClass(), "Couldn't reload the constructor '" + ctor
                            + "' to instantiate the bean '" + beanName + "' . Have you removed the "
                            + "required constructor without updating the bean definition?", ex);
                }
            //}
        }

        return super.instantiate(beanDefinition, beanName, owner, constructorToUse, args);
    }

    /**
     * <p>Return an instance of the bean with the given name in this factory,
     * creating it via the given factory method.</p>
     *
     * @param beanDefinition bean definition
     * @param beanName       name of the bean when it's created in this context.
     *                       The name can be <code>null</code> if we're autowiring a bean
     *                       that doesn't belong to the factory.
     * @param owner          owning BeanFactory
     * @param factoryBean    the factory bean instance to call the factory method on,
     *                       or <code>null</code> in case of a static factory method
     * @param factoryMethod  the factory method to use
     * @param args           the factory method arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation failed
     */
    public Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
                              Object factoryBean, Method factoryMethod, Object[] args)
    {
        // The factory method which we'll use to instantiate the bean.
        Method factoryMethodToUse = factoryMethod;

        // Determine whether the given bean definition supports a refresh operation,
        // i.e. if a refresh metadata attribute has been attached to it already.
        //RefreshableBeanAttribute refreshableAttribute =
        //        (RefreshableBeanAttribute) beanDefinition.getAttribute(
        //                RefreshableBeanAttribute.REFRESHABLE_BEAN_ATTRIBUTE);
        boolean refreshableAttribute = (beanDefinition.getBeanClass().getClassLoader() instanceof ThrowAwayClassloader);

        if (refreshableAttribute)
        {
            factoryMethodToUse = (Method) beanDefinition.getAttribute(CACHED_FACTORY_METHOD);
            if (factoryMethodToUse == null)
            {
                try
                {
                    // Reload the factory methods to use. The problem is that the given factory method possibly
                    // references an outdated Class object, which means, that if we used the given factory method
                    // to instantiate another object, we would end up with a ClassCastException as the given
                    // factory bean has already been reloaded.
                    factoryMethodToUse = beanDefinition.getBeanClass().getMethod(
                            factoryMethod.getName(), factoryMethod.getParameterTypes());

                    // Cache the factory method so that we don't have to use reflection every time.
                    beanDefinition.setAttribute(CACHED_FACTORY_METHOD, factoryMethodToUse);
                }
                catch (NoSuchMethodException ex)
                {
                    throw new BeanInstantiationException(
                            beanDefinition.getBeanClass(), "Couldn't reload the factory method '" + factoryMethod
                            + "' to instantiate the bean '" + beanName + "' . Have you removed the required "
                            + "factory method without updating the bean definition?", ex);
                }
            }
        }

        return super.instantiate(beanDefinition, beanName, owner, factoryBean, factoryMethodToUse, args);
    }
}

