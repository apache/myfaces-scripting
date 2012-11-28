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

package org.apache.myfaces.extensions.scripting.mojarra.servlet;

import org.apache.myfaces.extensions.scripting.core.api.ClassLoaderService;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassLoaderUtils;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import java.util.ServiceLoader;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          This servlet request listener plugs in our throw away classloader
 *          which loads the compiled classes on the fly
 */
@WebListener
public class ClassloaderSetupListener implements ServletRequestListener
{
    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
        ClassLoaderUtils.getDefaultClassLoaderService().registerThrowAwayClassloader();
       /* ClassLoaderService utils =
                ServiceLoader.load(ClassLoaderService.class)
                        .iterator().next();
        utils.registerThrowAwayClassloader(); */
    }
}
