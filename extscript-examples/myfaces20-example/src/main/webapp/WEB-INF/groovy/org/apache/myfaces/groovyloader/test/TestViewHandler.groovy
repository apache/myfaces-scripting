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
package org.apache.myfaces.groovyloader.test

import javax.faces.application.ViewHandler
import javax.faces.context.FacesContext
import javax.faces.component.UIViewRoot

/**
 * @author Werner Punz
 */
class TestViewHandler extends ViewHandler {
  //setters and getters are added implicitely
  ViewHandler delegate;

  /**
   * needed for reloading
   */
  public TestViewHandler() {
    super();
  }

  public TestViewHandler(ViewHandler delegate) {
    super();
    this.delegate = delegate;
  }


  public Locale calculateLocale(FacesContext facesContext) {
    return delegate.calculateLocale(facesContext);
  }

  public String calculateRenderKitId(FacesContext facesContext) {
    return delegate.calculateRenderKitId(facesContext);
  }

  public UIViewRoot createView(FacesContext facesContext, String s) {
    return delegate.createView(facesContext, s);
  }

  public String getActionURL(FacesContext facesContext, String s) {
    return delegate.getActionURL(facesContext, s);
  }

  public String getResourceURL(FacesContext facesContext, String s) {
    return delegate.getResourceURL(facesContext, s);
  }

  public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) {
    println "hello world from our view handler2 RENDERVIEW"

    delegate.renderView(facesContext, uiViewRoot);
  }

  public UIViewRoot restoreView(FacesContext facesContext, String s) {

    return delegate.restoreView(facesContext, s);
  }

  public void writeState(FacesContext facesContext) {
    delegate.writeState(facesContext);
  }

}