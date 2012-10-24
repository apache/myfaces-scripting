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

import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;
import org.apache.myfaces.extensions.scripting.core.engine.ThrowAwayClassloader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class CompilationAwareRefreshableBeanFactory extends DefaultListableBeanFactory
{
    public CompilationAwareRefreshableBeanFactory(BeanFactory parentBeanFactory)
    {
        super(parentBeanFactory);
    }

    public CompilationAwareRefreshableBeanFactory()
    {
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
            try
            {
                //spring caches the constructor, we have to set it anew
                //this imposes a limitation to () constructors but
                //I guess we can live with it
                Constructor ctor = retVal.getConstructor();
                ReflectUtil.setField(beanDefinition,"resolvedConstructorOrFactoryMethod", ctor, true);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

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
}
