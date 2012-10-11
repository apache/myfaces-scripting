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

import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.engine.ThrowAwayClassloader;
import org.apache.myfaces.extensions.scripting.core.monitor.ClassResource;
import org.apache.myfaces.shared.util.ClassLoaderExtension;

import javax.servlet.ServletContext;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          The chainloader docks onto the forName handler
 *          in the myfaces classutils which load the
 *          artifacting classes, we dock onto this extension point
 *          neutrally by indirection over the MyFacesSPI
 */

public class CustomChainLoader extends ClassLoaderExtension
{

    ThrowAwayClassloader _loader = null;

    static class _Action implements PrivilegedExceptionAction<ThrowAwayClassloader>
    {
        ClassLoader _parent;

        _Action(ClassLoader parent)
        {
            this._parent = parent;
        }

        public ThrowAwayClassloader run()
        {
            return new ThrowAwayClassloader(_parent);
        }
    }

    public CustomChainLoader(ServletContext context)
    {
        try
        {
            _loader = AccessController.doPrivileged(
                    new _Action(ClassUtils.getContextClassLoader())
            );
        }
        catch (PrivilegedActionException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Class forName(String name)
    {
        if (name.endsWith(";"))
        {
            name = name.substring(1, name.length() - 1);
        }

        if (name.startsWith("java.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return null;
        if (name.startsWith("javax.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return null;
        else if (name.startsWith("com.sun")) /*internal java specific namespace*/
            return null;
        else if (name.startsWith("sun.")) /*internal java specific namespace*/
            return null;
        else if (name.startsWith("org.apache") && !name.startsWith("org.apache.myfaces"))
        {
            return null;
        } else if (name.startsWith("org.apache") && name.startsWith("org.apache.myfaces.config"))
        {
            return null;
        } else if (name.startsWith("org.apache") && name.startsWith("org.apache.myfaces.spi"))
        {
            return null;
        } else if (name.startsWith("org.apache") && name.startsWith("org.apache.myfaces.application"))
        {
            return null;
        }
        //
        try
        {
            return loadClass(name);
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }

    private Class loadClass(String name) throws ClassNotFoundException
    {
        File targetDirectory = WeavingContext.getInstance().getConfiguration().getCompileTarget();
        File target = ClassUtils.classNameToFile(targetDirectory.getAbsolutePath(), name);
        if (!target.exists()) return null;
        //otherwise check if tainted and if not simply return the class stored

        ClassResource resource = (ClassResource) WeavingContext.getInstance().getResource(name);
        if (resource == null)
        {
            return null;
        }
        if (resource.isTainted() || resource.getAClass() == null)
        {
            //load the class via the throw away classloader
            return _loader.loadClass(name);
        } else
        {
            return resource.getAClass();
        }

    }

}
