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
package org.apache.myfaces.blank;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.application.ResourceHandlerImpl;
import org.apache.myfaces.application.ResourceHandlerSupport;
import org.apache.myfaces.scripting.api.Decorated;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * A simple delegating resource handler
 * which is supposed to pick up resources from a
 * given location if the Weaving configuration
 * has the parameter present
 */
class SourceResourceHandler extends ResourceHandlerImpl implements Decorated {

    ResourceHandler _delegate = null;
    Log log = LogFactory.getLog(this.getClass());
    ResourceHandlerSupport _sourceHandlerSupport = null;

    public SourceResourceHandler(ResourceHandler delegate) {
        _delegate = delegate;
    }

    public Resource createResource(String resourceName) {
        log.info("[EXT-SCRIPTUNG]" + resourceName);
        return _delegate.createResource(resourceName);
    }

    public Resource createResource(String resourceName, String libraryName) {
        log.info("[EXT-SCRIPTUNG]" + resourceName + "-" + libraryName);
        return _delegate.createResource(resourceName, libraryName);
    }

    public Resource createResource(String resourceName, String libraryName, String contentType) {
        log.info("[EXT-SCRIPTUNG]" + resourceName);
        return _delegate.createResource(resourceName, libraryName, contentType);
    }

    public String getRendererTypeForResourceName(String resourceName) {
        return _delegate.getRendererTypeForResourceName(resourceName);
    }

    public void handleResourceRequest(FacesContext context) throws IOException {
        _delegate.handleResourceRequest(context);
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
}
