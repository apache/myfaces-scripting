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

package org.apache.myfaces.extensions.scripting.jsf.resources;

import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.FacesContext;
import java.io.File;
import java.util.List;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * A simplified source resource handler which loads resources
 * from a given directory which is not the webapp directory
 */

public class SourceResourceHandler extends ResourceHandlerWrapper
{
    private final ResourceHandler _wrapped;

    public SourceResourceHandler(ResourceHandler wrapped)
    {
        _wrapped = wrapped;
    }

    public Resource createResource(final String resourceName, final String libraryName)
    {
        List<String> resourceRoots = WeavingContext.getInstance().getConfiguration().getResourceDirs();
        if (resourceRoots == null || resourceRoots.isEmpty())
        {
            return _wrapped.createResource(resourceName, libraryName);
        }

        for (String resourceRoot : resourceRoots)
        {
            File resourceFile = new File(buildStdResourceDir(resourceName, libraryName, resourceRoot));
            if (resourceFile.exists())
            {
                return new SourceResource(libraryName, resourceName, resourceFile);
            }
            resourceFile = new File(buildMetaInfResourceDir(resourceName, libraryName, resourceRoot));
            if (resourceFile.exists())
            {
                return new SourceResource(libraryName, resourceName, resourceFile);
            }
            resourceFile = new File(buildRootResourceDir(resourceName, libraryName, resourceRoot));
            if (resourceFile.exists())
            {
                return new SourceResource(libraryName, resourceName, resourceFile);
            }
        }
        return _wrapped.createResource(resourceName, libraryName);
    }

    public Resource createResource(final String resourceName, final String libraryName, String contentType)
    {
        List<String> resourceRoots = WeavingContext.getInstance().getConfiguration().getResourceDirs();
        if (resourceRoots == null || resourceRoots.isEmpty())
        {
            return _wrapped.createResource(resourceName, libraryName);
        }

        for (String resourceRoot : resourceRoots)
        {
            File resourceFile = new File(buildStdResourceDir(resourceName, libraryName, resourceRoot));
            if (resourceFile.exists())
            {
                return new SourceResource(libraryName, resourceName, resourceFile);
            }
            resourceFile = new File(buildMetaInfResourceDir(resourceName, libraryName, resourceRoot));
            if (resourceFile.exists())
            {
                return new SourceResource(libraryName, resourceName, resourceFile);
            }
            resourceFile = new File(buildRootResourceDir(resourceName, libraryName, resourceRoot));
            if (resourceFile.exists())
            {
                return new SourceResource(libraryName, resourceName, resourceFile);
            }

        }
        return _wrapped.createResource(resourceName, libraryName);
    }

    private String buildMetaInfResourceDir(String resourceName, String libraryName, String resourceRoot)
    {
        return resourceRoot + "/META-INF/resources/" + libraryName + "/" + resourceName;
    }

    private String buildStdResourceDir(String resourceName, String libraryName, String resourceRoot)
    {
        return resourceRoot + "/resources/" + libraryName + "/" + resourceName;
    }

    private String buildRootResourceDir(String resourceName, String libraryName, String resourceRoot)
    {
        return resourceRoot + "/" + libraryName + "/" + resourceName;
    }

    @Override
    public boolean isResourceRequest(FacesContext context)
    {
        return super.isResourceRequest(context);
    }

    @Override
    public ResourceHandler getWrapped()
    {
        return _wrapped;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
