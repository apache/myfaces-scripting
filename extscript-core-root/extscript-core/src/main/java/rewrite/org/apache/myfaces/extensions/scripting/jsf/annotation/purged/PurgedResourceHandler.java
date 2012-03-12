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
package rewrite.org.apache.myfaces.extensions.scripting.jsf.annotation.purged;


import rewrite.org.apache.myfaces.extensions.scripting.core.api.Decorated;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedResourceHandler extends ResourceHandler implements Decorated
{

    private static final String DOES_NOT_EXIST = "Resource Handler does not exist";

    ResourceHandler _delegate;

    public PurgedResourceHandler(ResourceHandler delegate) {
        _delegate = delegate;
    }

    @Override
    public Resource createResource(String resourceName) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public String getRendererTypeForResourceName(String resourceName) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void handleResourceRequest(FacesContext context) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public boolean isResourceRequest(FacesContext context) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public boolean libraryExists(String libraryName) {
        return false;
    }

    public ResourceHandler getDelegate() {
        return _delegate;
    }

    public void setDelegate(ResourceHandler delegate) {
        _delegate = delegate;
    }
}
