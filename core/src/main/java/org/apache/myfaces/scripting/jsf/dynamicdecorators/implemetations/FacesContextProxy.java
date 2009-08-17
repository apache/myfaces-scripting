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

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.render.RenderKit;
import javax.faces.component.UIViewRoot;
import javax.el.ELContext;
import java.util.Iterator;

/**
 * A reloading, weaving  faces context
 * this is needed because groovy fails on
 * the introspection of the standard java myfaces
 * faces context due to pending references
 * of the _impl into the portlet context
 * not sure if this works in portlets
 * though
 *
 * @author Werner Punz
 */
public class FacesContextProxy extends FacesContext implements Decorated {

    public FacesContext _delegate = null;



    public ELContext getELContext() {
        return _delegate.getELContext();
    }

    public Application getApplication() {
        return _delegate.getApplication();
    }

    public Iterator<String> getClientIdsWithMessages() {
        return _delegate.getClientIdsWithMessages();
    }

    public ExternalContext getExternalContext() {
        return _delegate.getExternalContext();
    }

    public FacesMessage.Severity getMaximumSeverity() {
        return _delegate.getMaximumSeverity();
    }

    public Iterator<FacesMessage> getMessages() {
        return _delegate.getMessages();
    }

    public Iterator<FacesMessage> getMessages(String s) {
        return _delegate.getMessages(s);
    }

    public RenderKit getRenderKit() {
        return _delegate.getRenderKit();
    }

    public boolean getRenderResponse() {
        return _delegate.getRenderResponse();
    }

    public boolean getResponseComplete() {
        return _delegate.getResponseComplete();
    }

    public ResponseStream getResponseStream() {
        return _delegate.getResponseStream();
    }

    public void setResponseStream(ResponseStream responseStream) {
        _delegate.setResponseStream(responseStream);
    }

    public ResponseWriter getResponseWriter() {
        return _delegate.getResponseWriter();
    }

    public void setResponseWriter(ResponseWriter responseWriter) {
        _delegate.setResponseWriter(responseWriter);
    }

    public UIViewRoot getViewRoot() {
        return _delegate.getViewRoot();
    }

    public void setViewRoot(UIViewRoot uiViewRoot) {
        _delegate.setViewRoot(uiViewRoot);
    }

    public void addMessage(String s, FacesMessage facesMessage) {
        _delegate.addMessage(s, facesMessage);
    }

    public void release() {
        _delegate.release();
    }

    public void renderResponse() {
        _delegate.renderResponse();
    }

    public void responseComplete() {
        _delegate.responseComplete();
    }


    public FacesContextProxy(FacesContext delegate) {
        _delegate = delegate;
    }

    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
