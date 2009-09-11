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

import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ProxyUtils;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import javax.servlet.ServletRequest;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Weaving renderkit which
 * acts as a proxy factory for
 * our internal reloading referers
 *
 * @author Werner Punz
 */
public class RenderkitProxy extends RenderKit implements Decorated {

    RenderKit _delegate = null;


    public RenderkitProxy(RenderKit delegate) {
        _delegate = delegate;
    }


    public void addRenderer(String s, String s1, Renderer renderer) {
        weaveDelegate();
        //wo do it brute force here because we have sometimes casts and hence cannot rely on proxies
        //renderers itself are flyweight patterns which means they are shared over objects
        renderer = (Renderer) reloadInstance(renderer);
        _delegate.addRenderer(s, s1, renderer);
    }

    public Renderer getRenderer(String s, String s1) {
        weaveDelegate();
        return  (Renderer) reloadInstance(_delegate.getRenderer(s, s1));
    }

    public ResponseStateManager getResponseStateManager() {
        weaveDelegate();
        return _delegate.getResponseStateManager();
    }

    public ResponseWriter createResponseWriter(Writer writer, String s, String s1) {
        weaveDelegate();
        return (ResponseWriter) reloadInstance(_delegate.createResponseWriter(writer, s, s1));
    }

    public ResponseStream createResponseStream(OutputStream outputStream) {
        weaveDelegate();
        return (ResponseStream) reloadInstance( _delegate.createResponseStream(outputStream));
    }

    public Object getDelegate() {
        return _delegate;  
    }


     private final void weaveDelegate() {
        _delegate = (RenderKit) ProxyUtils.getWeaver().reloadScriptingInstance(_delegate);
    }

    private final Object reloadInstance(Object instance) {
        if(instance == null) {
            return null;
        }
        if (ProxyUtils.isDynamic(instance.getClass()) && !alreadyWovenInRequest(instance.toString())) {
            instance = ProxyUtils.getWeaver().reloadScriptingInstance(instance);
            alreadyWovenInRequest(instance.toString());
        }
        return instance;
    }


    private final boolean alreadyWovenInRequest(String clazz) {
        //portlets now can be enabled thanks to the jsf2 indirections regarding the external context
        ServletRequest req = (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
           if (req.getAttribute(ScriptingConst.SCRIPTING_REQUSINGLETON + clazz) == null) {
            req.setAttribute(ScriptingConst.SCRIPTING_REQUSINGLETON + clazz, "");
            return false;
        }
        return true;
    }

}
