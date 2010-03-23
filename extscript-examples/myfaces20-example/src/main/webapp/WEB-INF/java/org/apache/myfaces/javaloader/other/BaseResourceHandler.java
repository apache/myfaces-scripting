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
package org.apache.myfaces.javaloader.other;

import javax.faces.application.ResourceHandler;
import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class BaseResourceHandler extends ResourceHandler {

    ResourceHandler _delegate;

    public BaseResourceHandler(ResourceHandler delegate) {
        _delegate = delegate;
    }

    public Resource createResource(String resourceName) {
        return _delegate.createResource(resourceName);
    }

    public Resource createResource(String resourceName, String libraryName) {
        return _delegate.createResource(resourceName, libraryName);
    }

    public Resource createResource(String resourceName, String libraryName, String contentType) {
        return _delegate.createResource(resourceName, libraryName, contentType);
    }

    public String getRendererTypeForResourceName(String resourceName) {
        return _delegate.getRendererTypeForResourceName(resourceName);
    }

    public void handleResourceRequest(FacesContext context) throws java.io.IOException {
        _delegate.handleResourceRequest(context);
    }

    public boolean isResourceRequest(FacesContext context) {
        return _delegate.isResourceRequest(context);
    }

    public boolean libraryExists(String libraryName) {
        return _delegate.libraryExists(libraryName);
    }
}
