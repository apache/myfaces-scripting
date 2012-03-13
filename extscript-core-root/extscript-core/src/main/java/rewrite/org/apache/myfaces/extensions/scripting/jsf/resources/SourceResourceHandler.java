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
package rewrite.org.apache.myfaces.extensions.scripting.jsf.resources;


import rewrite.org.apache.myfaces.extensions.scripting.core.api.Decorated;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.net.URL;

/**
 * A simple delegating resource handler
 * which is supposed to pick up resources from a
 * given location if the Weaving configuration
 * has the parameter present
 */
public class SourceResourceHandler extends ResourceHandlerImpl implements Decorated
{

    ResourceHandler _delegate = null;
    ResourceHandlerSupport _sourceHandlerSupport = null;

    public SourceResourceHandler(ResourceHandler delegate) {
        _delegate = delegate;
    }

    public Resource createResource(String resourceName) {
        Resource retVal = super.createResource(resourceName);
        if (retVal != null)
            return retVal;

        return _delegate.createResource(resourceName);
    }

    public Resource createResource(String resourceName, String libraryName) {
        Resource retVal = super.createResource(resourceName, libraryName);
        if (retVal != null)
            return retVal;

        return _delegate.createResource(resourceName, libraryName);
    }

    public Resource createResource(String resourceName, String libraryName, String contentType) {
        Resource retVal = super.createResource(resourceName, libraryName, contentType);

        if (retVal != null)
            return retVal;

        return _delegate.createResource(resourceName, libraryName, contentType);
    }

    public String getRendererTypeForResourceName(String resourceName) {

        String retVal = super.getRendererTypeForResourceName(resourceName);
        if (retVal != null)
            return retVal;

        return _delegate.getRendererTypeForResourceName(resourceName);
    }

    public void handleResourceRequest(FacesContext context) throws IOException {
        super.handleResourceRequest(context);
    }

    public boolean isResourceRequest(FacesContext context) {
        return _delegate.isResourceRequest(context);
    }

    public boolean libraryExists(String libraryName) {
        return _delegate.libraryExists(libraryName);
    }

    public ResourceHandler getDelegate() {
        return _delegate;
    }

    @Override
    /**
     * central override of this class it provides a new handler
     * support class which allows source pickups after the
     * JSF2 specified restful algorithms
     *
     * @return A support instance which allows also source pickups from resources additionally to the
     *              default deployment and jar pickups
     */
    protected ResourceHandlerSupport getResourceHandlerSupport() {
        if (_sourceHandlerSupport == null) {
            _sourceHandlerSupport = new SourceResourceHandlerSupport();
        }
        return _sourceHandlerSupport;
    }

    protected ResourceMeta deriveResourceMeta(ResourceLoader resourceLoader,
                                              String resourceName, String libraryName) {
        String localePrefix = getLocalePrefixForLocateResource();
        String resourceVersion = null;
        String libraryVersion = null;
        ResourceMeta resourceId = null;

        //1. Try to locate resource in a localized path
        if (localePrefix != null) {
            if (null != libraryName) {
                String pathToLib = localePrefix + '/' + libraryName;
                libraryVersion = resourceLoader.getLibraryVersion(pathToLib);

                if (null != libraryVersion) {
                    String pathToResource = localePrefix + '/'
                            + libraryName + '/' + libraryVersion + '/'
                            + resourceName;
                    resourceVersion = resourceLoader
                            .getResourceVersion(pathToResource);
                } else {
                    String pathToResource = localePrefix + '/'
                            + libraryName + '/' + resourceName;
                    resourceVersion = resourceLoader
                            .getResourceVersion(pathToResource);
                }

                if (!(resourceVersion != null && ResourceLoader.VERSION_INVALID.equals(resourceVersion))) {
                    resourceId = resourceLoader.createResourceMeta(localePrefix, libraryName,
                            libraryVersion, resourceName, resourceVersion);
                }
            } else {
                resourceVersion = resourceLoader
                        .getResourceVersion(localePrefix + '/' + resourceName);
                if (!(resourceVersion != null && ResourceLoader.VERSION_INVALID.equals(resourceVersion))) {
                    resourceId = resourceLoader.createResourceMeta(localePrefix, null, null,
                            resourceName, resourceVersion);
                } else if (resourceVersion == ResourceLoader.VERSION_INVALID) {
                    resourceId = resourceLoader.createResourceMeta(null, libraryName,
                            null, resourceName, null);
                }
            }

            if (resourceId != null) {
                URL url = resourceLoader.getResourceURL(resourceId);
                if (url == null) {
                    resourceId = null;
                }
            }
        }

        //2. Try to localize resource in a non localized path
        if (resourceId == null) {
            if (null != libraryName) {
                libraryVersion = resourceLoader.getLibraryVersion(libraryName);

                if (null != libraryVersion) {
                    String pathToResource = (libraryName + '/' + libraryVersion
                            + '/' + resourceName);
                    resourceVersion = resourceLoader
                            .getResourceVersion(pathToResource);
                } else {
                    String pathToResource = (libraryName + '/'
                            + resourceName);
                    resourceVersion = resourceLoader
                            .getResourceVersion(pathToResource);
                }

                if (!(resourceVersion != null && ResourceLoader.VERSION_INVALID.equals(resourceVersion))) {
                    resourceId = resourceLoader.createResourceMeta(null, libraryName,
                            libraryVersion, resourceName, resourceVersion);
                } else if (resourceVersion == ResourceLoader.VERSION_INVALID) {
                    resourceId = resourceLoader.createResourceMeta(null, libraryName,
                            libraryVersion, resourceName, null);
                }
            } else {
                resourceVersion = resourceLoader
                        .getResourceVersion(resourceName);

                if (!(resourceVersion != null && ResourceLoader.VERSION_INVALID.equals(resourceVersion))) {
                    resourceId = resourceLoader.createResourceMeta(null, null, null,
                            resourceName, resourceVersion);
                } else if (resourceVersion == ResourceLoader.VERSION_INVALID) {
                    resourceId = resourceLoader.createResourceMeta(null, null,
                            null, resourceName, null);
                }
            }

            if (resourceId != null) {
                URL url = resourceLoader.getResourceURL(resourceId);
                if (url == null) {
                    resourceId = null;
                }
            }
        }

        return resourceId;
    }
}
