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

package org.apache.myfaces.extensions.scripting.cdi.startup;

//import org.apache.myfaces.extensions.scripting.cdi.core.CDIThrowAwayClassloader;

import org.apache.myfaces.extensions.scripting.cdi.core.CDIThrowAwayClassloader;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassLoaderUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p>&nbsp;</p>
 *          An extension for cdi which does the initial lifecycle
 *          trigger from CDI instead of JSF since CDI is enabled
 *          before JSF we have to do it that way
 *          <p>&nbsp;</p>
 *          Stage 1 of our startup cycle
 */

public class StartupExtension implements Extension
{
    static ThreadLocal<ClassLoader> _classLoaderHolder = new ThreadLocal<ClassLoader>();

    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd)
    {
        //We have to trigger our initial lifecycle with
        //the compile runs but not with the daemon thread
        //after that we can load the classes
        //by temporarily plugging in our throw away classloader
        _classLoaderHolder.set(Thread.currentThread().getContextClassLoader());

        Thread.currentThread().setContextClassLoader(new CDIThrowAwayClassloader(Thread.currentThread().getContextClassLoader()));
        //since we have an override we now register
        //ClassLoaderUtils.getDefaultClassLoaderService().registerThrowAwayClassloader();
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd)
    {

    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation abv)
    {
        //here we unplug our temporary classloader
       Thread.currentThread().setContextClassLoader(_classLoaderHolder.get());
    }
}
