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

package org.apache.myfaces.extensions.scripting.cdi.adapters;

import org.apache.myfaces.extensions.scripting.cdi.core.CDIThrowAwayClassloader;
import org.apache.myfaces.extensions.scripting.core.api.ClassLoaderService;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassLoaderUtils;
import org.apache.myfaces.extensions.scripting.core.engine.ThrowAwayClassloader;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ClassLoaderServiceImpl implements ClassLoaderService
{
    volatile static ClassLoader _oldClassLoader = null;

    @Override
    public void registerThrowAwayClassloader()
    {
         //we do not have the luxury of a pluggable classloading extensions like in myfaces
        // instead we have to provide our own classloader which is hooked in from time to time into the running system
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();
        boolean found = false;
        ClassLoader parent = loader;
        while(parent != null && !found) {
            found = parent instanceof CDIThrowAwayClassloader;
            if(!found) {
                parent = parent.getParent();
            }
        }
        if(found) {
            return;
        }
        //in case of an unchanged classloader we can recycle our old throw away classloader
        if(_oldClassLoader != null && loader.equals(_oldClassLoader.getParent())) {
            Thread.currentThread().setContextClassLoader(_oldClassLoader);
        } else {
            _oldClassLoader = new CDIThrowAwayClassloader(loader);
            Thread.currentThread().setContextClassLoader(_oldClassLoader);
        }
    }

    @Override
    public int getPriority()
    {
        return 3;
    }
}
