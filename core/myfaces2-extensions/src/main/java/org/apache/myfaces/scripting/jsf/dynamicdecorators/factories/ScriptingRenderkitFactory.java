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
package org.apache.myfaces.scripting.jsf.dynamicdecorators.factories;

import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.core.util.ProxyUtils;
import org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations.RenderkitProxy;

import javax.faces.render.RenderKitFactory;
import javax.faces.render.RenderKit;
import javax.faces.context.FacesContext;
import java.util.Iterator;

/**
 * Scripting enabled renderkit factory
 *
 * @author Werner Punz
 */
public class ScriptingRenderkitFactory extends RenderKitFactory implements Decorated {

    boolean scriptingEnabled = false;

    public ScriptingRenderkitFactory(RenderKitFactory delegate) {
        _delegate = delegate;
        scriptingEnabled = ProxyUtils.isScriptingEnabled();
    }

    public void addRenderKit(String s, RenderKit renderKit) {
        if (renderKit != null && !(renderKit instanceof RenderkitProxy))
            renderKit = new RenderkitProxy(renderKit);

        _delegate.addRenderKit(s, renderKit);
    }

    public RenderKit getRenderKit(FacesContext facesContext, String s) {
        RenderKit retVal = _delegate.getRenderKit(facesContext, s);
        if (retVal != null && !(retVal instanceof RenderkitProxy))
            retVal = new RenderkitProxy(retVal);
        return retVal;
    }

    public Iterator getRenderKitIds() {
        return _delegate.getRenderKitIds();
    }


    public void setDelegate(RenderKitFactory delegate) {
        _delegate = delegate;
    }

    @Override
    public RenderKitFactory getWrapped() {
        return _delegate.getWrapped();
    }

    RenderKitFactory _delegate = null;

    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
