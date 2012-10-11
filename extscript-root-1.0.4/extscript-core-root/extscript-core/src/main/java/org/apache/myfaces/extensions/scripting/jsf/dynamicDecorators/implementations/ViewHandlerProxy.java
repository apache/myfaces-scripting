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
package org.apache.myfaces.extensions.scripting.jsf.dynamicDecorators.implementations;


import org.apache.myfaces.extensions.scripting.core.api.Decorated;
import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Scripting enabled View Handler
 *
 * @author Werner Punz
 */
public class ViewHandlerProxy extends ViewHandler implements Decorated
{

    ViewHandler _delegate = null;

    private void weaveDelegate() {
        if (_delegate != null) {
            _delegate = (ViewHandler) WeavingContext.getInstance().reload(_delegate,
                    ScriptingConst.ARTIFACT_TYPE_VIEWHANDLER);
        }
    }

    public ViewHandlerProxy(ViewHandler delegate) {
        _delegate = delegate;
    }

    public String calculateCharacterEncoding(FacesContext facesContext) {
        weaveDelegate();
        return _delegate.calculateCharacterEncoding(facesContext);
    }

    public Locale calculateLocale(FacesContext facesContext) {
        weaveDelegate();
        return _delegate.calculateLocale(facesContext);
    }

    public String calculateRenderKitId(FacesContext facesContext) {
        weaveDelegate();
        return _delegate.calculateRenderKitId(facesContext);
    }

    public UIViewRoot createView(FacesContext facesContext, String s) {
        weaveDelegate();
        return _delegate.createView(facesContext, s);
    }

    public String getActionURL(FacesContext facesContext, String s) {
        weaveDelegate();
        return _delegate.getActionURL(facesContext, s);
    }

    public String getResourceURL(FacesContext facesContext, String s) {
        weaveDelegate();
        return _delegate.getResourceURL(facesContext, s);
    }

    public void initView(FacesContext facesContext) throws FacesException {
        weaveDelegate();
        _delegate.initView(facesContext);
    }

    public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException, FacesException {
        weaveDelegate();
        _delegate.renderView(facesContext, uiViewRoot);
    }

    public UIViewRoot restoreView(FacesContext facesContext, String s) {
        weaveDelegate();
        return _delegate.restoreView(facesContext, s);
    }

    public void writeState(FacesContext facesContext) throws IOException {
        weaveDelegate();
        _delegate.writeState(facesContext);
    }


    @Override
    public String deriveViewId(FacesContext facesContext, String s) {
        weaveDelegate();
        return _delegate.deriveViewId(facesContext, s);
    }

    @Override
    public String getBookmarkableURL(FacesContext facesContext, String s, Map<String, List<String>> stringListMap, boolean b) {
        return super.getBookmarkableURL(facesContext, s, stringListMap, b);
    }

    @Override
    public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext facesContext, String s) {
        weaveDelegate();
        return _delegate.getViewDeclarationLanguage(facesContext, s);
    }

    @Override
    public String getRedirectURL(FacesContext facesContext, String s, Map<String, List<String>> stringListMap, boolean b) {
        weaveDelegate();
        return _delegate.getRedirectURL(facesContext, s, stringListMap, b);
    }

    public Object getDelegate() {
        return _delegate;
    }
}
