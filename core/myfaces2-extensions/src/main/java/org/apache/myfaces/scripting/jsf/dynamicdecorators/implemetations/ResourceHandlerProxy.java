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
package org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations;

import org.apache.myfaces.scripting.core.util.ProxyUtils;

import javax.faces.application.ResourceHandler;
import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ResourceHandlerProxy extends ResourceHandler {

    ResourceHandler _delegate = null;

    public ResourceHandlerProxy() {
        super();
    }

    public ResourceHandlerProxy(ResourceHandler delegate) {
        super();
        this._delegate = delegate;
    }

    public Resource createResource(String s) {
        return _delegate.createResource(s);
    }

    public Resource createResource(String s, String s1) {
        return _delegate.createResource(s, s1);
    }

    public Resource createResource(String s, String s1, String s2) {
        return _delegate.createResource(s, s1, s2);
    }

    public String getRendererTypeForResourceName(String s) {
        return _delegate.getRendererTypeForResourceName(s);
    }

    public void handleResourceRequest(FacesContext facesContext) {
        weaveDelegate();
        _delegate.handleResourceRequest(facesContext);
    }

    public boolean isResourceRequest(FacesContext facesContext) {
        return _delegate.isResourceRequest(facesContext);
    }

    public boolean libraryExists(String s) {
        return _delegate.libraryExists(s);
    }

    private void weaveDelegate() {
        if (_delegate != null)
            _delegate = (ResourceHandler) ProxyUtils.getWeaver().reloadScriptingInstance(_delegate);
    }

}
