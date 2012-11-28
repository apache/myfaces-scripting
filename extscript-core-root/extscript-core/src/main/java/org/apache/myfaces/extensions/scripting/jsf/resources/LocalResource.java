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

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class LocalResource extends Resource
{
    File _location;

    public LocalResource(final String libraryName, final String resourceName, File location)
    {
        setLibraryName(libraryName);
        setResourceName(resourceName);
        setContentType(FacesContext.getCurrentInstance().getExternalContext().getMimeType(resourceName));
        _location = location;
    }

    public LocalResource(final String libraryName, final String resourceName, File location, String contentType)
    {
        setLibraryName(libraryName);
        setResourceName(resourceName);
        setContentType(contentType);
        _location = location;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return new FileInputStream(_location);

    }

    @Override
    public Map<String, String> getResponseHeaders()
    {
        return new HashMap<String, String>();
    }

    @Override
    public String getRequestPath()
    {
        final FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String mapping = calculatePrefixMapping(externalContext.getRequestServletPath(),
                externalContext.getRequestPathInfo());
        boolean prefixMapping = true;
        if (mapping == null)
        {
            prefixMapping = false;
            mapping = calculatePostfixMapping(externalContext.getRequestServletPath(),
                    externalContext.getRequestPathInfo());
        }
        if (prefixMapping)
        {
            return context
                    .getApplication()
                    .getViewHandler()
                    .getResourceURL(
                            context, mapping +
                            ResourceHandler.RESOURCE_IDENTIFIER + "/" + getResourceName()
                            + "?ln=" + getLibraryName());
        } else
        {
            return context
                    .getApplication()
                    .getViewHandler()
                    .getResourceURL(
                            context,
                            ResourceHandler.RESOURCE_IDENTIFIER + "/" + getResourceName()
                                    + mapping + "?ln=" + getLibraryName());
        }
    }

    String calculatePrefixMapping(String requestServletPath, String requestPathInfo)
    {
        if (requestPathInfo == null) return null;
        return requestServletPath;
    }

    String calculatePostfixMapping(String requestServletPath, String requestPathInfo)
    {
        int slashPos = requestServletPath.lastIndexOf('/');
        int extensionPos = requestServletPath.lastIndexOf('.');
        if (extensionPos > -1 && extensionPos > slashPos)
        {
            String extension = requestServletPath.substring(extensionPos);
            return extension;
        }
        return null;
    }

    @Override
    public URL getURL()
    {
        return null;
    }

    @Override
    public boolean userAgentNeedsUpdate(final FacesContext context)
    {
        return true;
    }
}
