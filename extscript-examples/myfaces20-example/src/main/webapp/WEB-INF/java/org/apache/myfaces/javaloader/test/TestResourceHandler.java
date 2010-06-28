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

package org.apache.myfaces.javaloader.test;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class TestResourceHandler extends ResourceHandler {

    ResourceHandler _delegate;

    Logger _logger = Logger.getLogger("TestResourceHandler");

    public TestResourceHandler(ResourceHandler delegate) {
        //bug in myfaces the resource handlers are attached twice
        while(delegate instanceof TestResourceHandler) {
            delegate = ((TestResourceHandler)delegate).getDelegate();
        }
        _delegate = delegate;
    }

    public TestResourceHandler() {
    }

    @Override
    public Resource createResource(String resourceName) {
        return _delegate.createResource(resourceName);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        return _delegate.createResource(resourceName, libraryName);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        return _delegate.createResource(resourceName, libraryName, contentType);
    }

    @Override
    public String getRendererTypeForResourceName(String resourceName) {
        return _delegate.getRendererTypeForResourceName(resourceName);
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        _delegate.handleResourceRequest(context);
    }

    @Override
    public boolean isResourceRequest(FacesContext context) {
        _logger.info("TestResourceHandler.isResourceRequest");

        return _delegate.isResourceRequest(context);
    }

    @Override
    public boolean libraryExists(String libraryName) {
        return _delegate.libraryExists(libraryName);
    }

    //we have to expose the internal delegate because we have
    //to copy it
    public ResourceHandler getDelegate() {
        return _delegate;
    }

    public void setDelegate(ResourceHandler delegate) {
        _delegate = delegate;
    }
}
