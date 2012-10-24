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

package org.apache.myfaces.extensions.scripting.spring.bean;

import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.engine.ThrowAwayClassloader;
import org.apache.myfaces.extensions.scripting.spring.bean.support.CompilationAwareInstantiationStrategy;
import org.apache.myfaces.extensions.scripting.spring.util.ProxyAopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class CompilationAwareRefreshableBeanFactory extends DefaultListableBeanFactory
{
    public CompilationAwareRefreshableBeanFactory(BeanFactory parentBeanFactory)
    {
        super(parentBeanFactory);
        setInstantiationStrategy(new CompilationAwareInstantiationStrategy());
    }

    public CompilationAwareRefreshableBeanFactory()
    {
        setInstantiationStrategy(new CompilationAwareInstantiationStrategy());
    }

    /**
     * We have to set our throw away classloader
     * here so that it is picked up by spring
     *
     * @param beanClassLoader
     */
    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader)
    {
        if (!(beanClassLoader instanceof ThrowAwayClassloader))
        {
            super.setBeanClassLoader(new ThrowAwayClassloader(beanClassLoader));
        } else
        {
            super.setBeanClassLoader(beanClassLoader);
        }
    }

    @Override
    protected Class resolveBeanClass(RootBeanDefinition mbd, String beanName)
    {
        //TODO drop the check whether the class has been loaded before
        //and then resolve the class from the classloader

        return super.resolveBeanClass(mbd, beanName);
    }

    // ------------------------------------------ AbstractBeanFactory methods

    @Override
    public Object getBean(final String name, final Class requiredType, final Object[] args) throws BeansException
    {
        Object bean = super.getBean(name, requiredType, args);

        BeanDefinition beanDefinition = getMergedBeanDefinition(name);
        //if (beanDefinition.hasAttribute(
        //        RefreshableBeanAttribute.REFRESHABLE_BEAN_ATTRIBUTE)) { // Try to minimize synchronizing ..
        // Note that the DefaultListableBeanFactory internally caches bean definitions,
        // so I think it's safe to use the return value as monitor for the synchronized
        // block.
        synchronized (beanDefinition)
        {
            // Obtain the metadata attribute of the bean definition that contains the
            // required information to determine wheter a refresh is required or not.
            //RefreshableBeanAttribute refreshableAttribute =
            //        (RefreshableBeanAttribute) beanDefinition.getAttribute(
            //                RefreshableBeanAttribute.REFRESHABLE_BEAN_ATTRIBUTE);
            if (requiresRefresh(bean, beanDefinition))
            {
                bean = refresh(bean, name, new ObjectFactory()
                {
                    public Object getObject() throws BeansException
                    {
                        return CompilationAwareRefreshableBeanFactory.super.getBean(name, requiredType, args);
                    }
                }, beanDefinition);
                //refreshableAttribute.executedRefresh();
            }
        }
        //}

        return bean;
    }

    private boolean requiresRefresh(Object bean, BeanDefinition beanDefinition)
    {
        //TODO add the refreshable classes from the event handler there
        Class clazz = ProxyAopUtils.resolveTargetClass(bean);
        return WeavingContext.getInstance().isDynamic(clazz);
    }

    @Override
    protected Class resolveBeanClass(RootBeanDefinition beanDefinition, String beanName, Class[] typesToMatch) throws CannotLoadBeanClassException
    {
        try
        {
            // Note that this implementation doesn't check if the bean class has
            // already been resolved previously (i.e. there's no check whether
            // mbd.getBeanClass() is null). In doing so, it's possible to
            // reload recompiled Class files.

            if (typesToMatch != null)
            {
                ClassLoader tempClassLoader = getTempClassLoader();
                if (tempClassLoader != null)
                {
                    if (tempClassLoader instanceof DecoratingClassLoader)
                    {
                        DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
                        for (int i = 0; i < typesToMatch.length; i++)
                        {
                            dcl.excludeClass(typesToMatch[i].getName());
                        }
                    }
                    String className = beanDefinition.getBeanClassName();
                    return (className != null ? ClassUtils.forName(className, tempClassLoader) : null);
                }
            }

            Class retVal = beanDefinition.resolveBeanClass(getBeanClassLoader());
            return retVal;
        }
        catch (ClassNotFoundException ex)
        {
            throw new CannotLoadBeanClassException(
                    beanDefinition.getResourceDescription(), beanName, beanDefinition.getBeanClassName(), ex);
        }
        catch (LinkageError err)
        {
            throw new CannotLoadBeanClassException(
                    beanDefinition.getResourceDescription(), beanName, beanDefinition.getBeanClassName(), err);
        }
    }

    protected Object refresh(Object beanInstance, String beanName,
                             ObjectFactory objectFactory, BeanDefinition beanDefinition)
    {
        // In the case of a prototype-scoped bean, we don't have to worry about copying
        // properties, etc. as it's definitely always a newly created beanInstance instance
        // and so it's safe to assume that we're using the most recent Class reference.
        if (!BeanDefinition.SCOPE_PROTOTYPE.equals(beanDefinition.getScope()))
        {
            // At first destroy the previously created bean (e.g. execute the according callbacks
            // if the bean implements the DisposableBean interface and remove it from the according
            // scope).
            if (beanDefinition.isSingleton())
            {
                destroySingleton(beanName);
            } else
            {
                destroyScopedBean(beanName);
            }

            // Now that the bean has already been destroyed, we'll create a new instance of it.
            Object newBeanInstance = objectFactory.getObject();

            // Copy the properties only if we're not dealing with a proxy. In that case the
            // BeanFactory will copy the properties anyway once the target bean gets recreated (in
            // fact, otherwise we would have to face dozens of InvocationTargetExceptions as it would
            // create a huge mess with different versions of different Class references).
            if (!ProxyAopUtils.isProxyClass(newBeanInstance.getClass()))
            {
                copyProperties(beanName, beanDefinition, beanInstance, newBeanInstance);
            }

            return newBeanInstance;
        } else
        {
            return beanInstance;
        }
    }

    /**
     * <p>Copies the property values of the given source bean into the given target bean. Note that
     * this method, in contrast to the {@link org.springframework.beans.BeanUtils#copyProperties(Object, Object)} method,
     * is aware of the possibility that copying properties from one bean to another could override newer versions of beans
     * that Spring has injected. </p>
     */
    private void copyProperties(String beanName, BeanDefinition beanDefinition, Object source, Object target)
    {
        // Definitely we don't want to mess around with properties of
        // any proxy objects, so we'll use its target class here.
        PropertyDescriptor[] targetDescriptors = BeanUtils.getPropertyDescriptors(
                ProxyAopUtils.resolveTargetClass(target));

        for (int i = 0; i < targetDescriptors.length; i++)
        {
            PropertyDescriptor targetDescriptor = targetDescriptors[i];
            if (targetDescriptor.getWriteMethod() != null)
            {
                PropertyDescriptor sourceDescriptor =
                        BeanUtils.getPropertyDescriptor(source.getClass(), targetDescriptor.getName());
                if (sourceDescriptor != null && sourceDescriptor.getReadMethod() != null)
                {
                    try
                    {
                        Object value = invokeMethod(sourceDescriptor.getReadMethod(), source, new Object[0]);
                        if (value instanceof ThrowAwayClassloader)
                        {
                            //spring let spring handle the property assignment for a dynamic class
                            //so that property reloads work
                        } else
                        {
                            invokeMethod(targetDescriptor.getWriteMethod(), target, new Object[]{value});
                        }

                        /*if (getReloadingClassLoader().isOutdated(value.getClass()) &&
                                beanDefinition.getPropertyValues().contains(targetDescriptor.getName())) {
                            // If the currenty property has been injected by Spring already and the
                            // source object returned an outdated object, keep the one that has been
                            // injected by Spring as it's more likely to be the most recent one.

                            if (logger.isWarnEnabled()) {
                                logger.warn(" This factory will not copy the property '" + targetDescriptor.getName() + "' of the bean '" +
                                        beanName + "' as the source object '" + source + "' only returns an outdated object '" + value + "'.");
                            }
                        }
                        else {
                        invokeMethod(targetDescriptor.getWriteMethod(), target, new Object[]{value});
                        /*}*/
                    }
                    catch (Throwable ex)
                    {
                        throw new FatalBeanException("Could not copy properties from source to target", ex);
                    }
                }
            }
        }
    }

    /**
     * <p>Invokes the given method on the given target with the given arguments. Note that
     * this method assumes that the method itself is already accessible (which should be the
     * case as it's sole purpose is to invoke getters and setters which have to be public
     * methods anyway).</p>
     */
    private static Object invokeMethod(Method method, Object target, Object[] args)
            throws InvocationTargetException, IllegalAccessException
    {
        if (!Modifier.isPublic(method.getDeclaringClass().getModifiers()))
        {
            method.setAccessible(true);
        }

        return method.invoke(target, args);
    }

}
