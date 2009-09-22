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
package org.apache.myfaces.scripting.jsf2.annotation;

import com.thoughtworks.qdox.model.JavaClass;

import java.util.Map;

import org.apache.myfaces.scripting.api.AnnotationScanListener;

import javax.faces.render.FacesRenderer;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class RendererImplementationListener extends MapEntityAnnotationScanner implements AnnotationScanListener {
    private static final String PAR_FAMILY = "componentFamily";
    private static final String PAR_RENDERERTYPE = "rendererType";
    private static final String PAR_RENDERKITID = "renderKitId";

    class AnnotationEntry {
        String componentFamily;
        String rendererType;
        String renderKitId;

        AnnotationEntry(String componentFamily, String rendererType, String renderKitId) {
            this.componentFamily = componentFamily;
            this.rendererType = rendererType;
            this.renderKitId = renderKitId;
        }

        public boolean equals(Object incoming) {
            if (!(incoming instanceof AnnotationEntry)) {
                return false;
            }
            AnnotationEntry toCompare = (AnnotationEntry) incoming;
            //handle null cases
            if ((componentFamily == null && toCompare.getComponentFamily() != null) ||
                (componentFamily != null && toCompare.getComponentFamily() == null) ||
                (rendererType == null && toCompare.getRendererType() != null) ||
                (rendererType != null && toCompare.getRendererType() == null) ||
                (renderKitId == null && toCompare.getRenderKitId() != null) ||
                (renderKitId != null && toCompare.getRenderKitId() == null)) {

                return false;
            } else if (componentFamily == null && toCompare.getComponentFamily() == null &&
                       rendererType == null && toCompare.getRendererType() == null &&
                       renderKitId == null && toCompare.getRenderKitId() == null) {
                return true;
            }

            return componentFamily.equals(toCompare.getComponentFamily()) &&
                   rendererType.equals(toCompare.getComponentFamily()) &&
                   renderKitId.equals(toCompare.getRenderKitId());
        }

        public String getComponentFamily() {
            return componentFamily;
        }

        public String getRendererType() {
            return rendererType;
        }

        public String getRenderKitId() {
            return rendererType;
        }
    }


    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesRenderer.class.getName());
    }


    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) params.get(PAR_FAMILY);
        String theDefault = (String) params.get(PAR_RENDERERTYPE);
        String renderKitId = (String) params.get(PAR_RENDERKITID);


        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);
        _alreadyRegistered.put(clazz.getName(), entry);

        //getApplication().getResourceBundle(entry.getComponentFamily(), clazz.getName()) ;
    }

    @Override
    protected void addEntity(JavaClass clazz, Map<String, Object> params) {
        String value = getAnnotatedStringParam(params, PAR_FAMILY);
        String theDefault = getAnnotatedStringParam(params, PAR_RENDERERTYPE);
        String renderKitId = (String) getAnnotatedStringParam(params, PAR_RENDERKITID);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);
        _alreadyRegistered.put(clazz.getFullyQualifiedName(), entry);

        //getApplication().addConverter(entry.getComponentFamily(), clazz.getFullyQualifiedName());
    }

    @Override
    protected boolean hasToReregister(Map params, Class clazz) {
        String value = (String) params.get(PAR_FAMILY);
        String theDefault = (String) params.get(PAR_RENDERERTYPE);
        String renderKitId = (String) params.get(PAR_RENDERKITID);


        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getName());
        if (alreadyRegistered == null) {
            return true;
        }

        return alreadyRegistered.equals(entry);
    }

    @Override
    protected boolean hasToReregister(Map params, JavaClass clazz) {
        String value = getAnnotatedStringParam(params, PAR_FAMILY);
        String theDefault = (String) getAnnotatedStringParam(params, PAR_RENDERERTYPE);
        String renderKitId = (String) getAnnotatedStringParam(params, PAR_RENDERKITID);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getFullyQualifiedName());
        if (alreadyRegistered == null) {
            return true;
        }

        return alreadyRegistered.equals(entry);
    }
}
