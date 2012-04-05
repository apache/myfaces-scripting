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

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * Initializer which stores the servlet context
 * for later non servlet based references.
 *
 * This is the first stage in the servlet lifecycle
 * and starts before the cdi container.
 *
 * Stage 0 of our startup cycle
 */

public class CDIServletContainerInitializer implements ServletContainerInitializer
{
    private static WeakReference<ServletContext> _contextHolder = null;

    public void onStartup(Set<Class<?>> c, ServletContext cx)
    {
        _contextHolder = new WeakReference(cx);
    }

    public static ServletContext getContext()
    {
        return _contextHolder.get();
    }

}
