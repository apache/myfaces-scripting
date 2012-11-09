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
package org.apache.myfaces.extensions.scripting.jsf.adapters;

import org.apache.myfaces.extensions.scripting.core.api.ImplementationService;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.jsf.adapters.handlers.MyFacesBeanHandler;

import javax.servlet.ServletContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          SPI for various myfaces related tasks
 */
public class MyFacesSPI implements ImplementationService
{
    CustomChainLoader _loader = null;
    MyFacesBeanHandler beanHandler = new MyFacesBeanHandler();

    public MyFacesSPI()
    {
    }

    public void registerClassloadingExtension(ServletContext context)
    {
        CustomChainLoader loader = new CustomChainLoader(context); //ReflectUtil.instantiate("extras.org.apache.myfaces.extensions
        // .scripting.servlet" +
        //".CustomChainLoader",
        //new Cast(ServletContext.class, context));
        ClassUtils.addClassLoadingExtension(loader, true);
        _loader = loader;
    }

    public void refreshManagedBeans()
    {
        beanHandler.refreshAllManagedBeans();
    }

    public Class forName(String clazz)
    {
        return _loader.forName(clazz);
    }

}
