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
package org.apache.myfaces.scripting.jsf2.annotation.purged;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;
import javax.faces.render.Renderer;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedComponent extends UIComponent{
    @Override
    public Map<String, Object> getAttributes() {
        return null;  
    }

    @Override
    public boolean isRendered() {
        return false;  
    }

    @Override
    public void setValueBinding(String name, ValueBinding binding) {
        
    }

    @Override
    public String getClientId(FacesContext context) {
        return null;  
    }

    @Override
    public String getFamily() {
        return null;  
    }

    @Override
    public String getId() {
        return null;  
    }

    @Override
    public void setId(String id) {
        
    }

    @Override
    public void setParent(UIComponent parent) {
        
    }

    @Override
    public UIComponent getParent() {
        return null;  
    }

    @Override
    public void setRendered(boolean rendered) {
        
    }

    @Override
    public String getRendererType() {
        return null;  
    }

    @Override
    public void setRendererType(String rendererType) {
        
    }

    @Override
    public boolean getRendersChildren() {
        return false;  
    }

    @Override
    public ValueBinding getValueBinding(String name) {
        return null;  
    }

    @Override
    public List<UIComponent> getChildren() {
        return null;  
    }

    @Override
    public int getChildCount() {
        return 0;  
    }

    @Override
    public UIComponent findComponent(String expr) {
        return null;  
    }

    @Override
    public Map<String, UIComponent> getFacets() {
        return null;  
    }

    @Override
    public UIComponent getFacet(String name) {
        return null;  
    }

    @Override
    public Iterator<UIComponent> getFacetsAndChildren() {
        return null;  
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        
    }

    @Override
    public void decode(FacesContext context) {
        
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        
    }

    @Override
    protected void addFacesListener(FacesListener listener) {
        
    }

    @Override
    protected FacesListener[] getFacesListeners(Class clazz) {
        return new FacesListener[0];  
    }

    @Override
    protected void removeFacesListener(FacesListener listener) {
        
    }

    @Override
    public void queueEvent(FacesEvent event) {
        
    }

    @Override
    public void processRestoreState(FacesContext context, Object state) {
        
    }

    @Override
    public void processDecodes(FacesContext context) {
        
    }

    @Override
    public void processValidators(FacesContext context) {
        
    }

    @Override
    public void processUpdates(FacesContext context) {
        
    }

    @Override
    public Object processSaveState(FacesContext context) {
        return null;  
    }

    @Override
    protected FacesContext getFacesContext() {
        return null;  
    }

    @Override
    protected Renderer getRenderer(FacesContext context) {
        return null;  
    }

    public Object saveState(FacesContext context) {
        return null;  
    }

    public void restoreState(FacesContext context, Object state) {
        
    }

    public boolean isTransient() {
        return false;  
    }

    public void setTransient(boolean newTransientValue) {
        
    }
}
