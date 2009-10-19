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
import org.apache.myfaces.scripting.api.AnnotationScanListener;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.ProxyUtils;
import org.apache.myfaces.scripting.jsf2.annotation.purged.PurgedComponent;
import org.apache.myfaces.scripting.jsf2.annotation.purged.PurgedRenderer;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class RendererImplementationListener extends MapEntityAnnotationScanner implements AnnotationScanListener {
    private static final String PAR_FAMILY = "componentFamily";
    private static final String PAR_RENDERERTYPE = "rendererType";
    private static final String PAR_RENDERKITID = "renderKitId";

    public RendererImplementationListener() {
        super(PAR_FAMILY, PAR_RENDERERTYPE, PAR_RENDERKITID);
    }


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
            return renderKitId;
        }
    }


    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesRenderer.class.getName());
    }


    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) getAnnotatedStringParam(params, PAR_FAMILY);
        String theDefault = (String) getAnnotatedStringParam(params, PAR_RENDERERTYPE);

        String renderKitId = getRenderKitId(params);
        RenderKit renderKit = getRenderkit(renderKitId);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);
        _alreadyRegistered.put(clazz.getName(), entry);

        if (log.isTraceEnabled()) {
            log.trace("addRenderer(" + renderKitId + ", "
                      + entry.getComponentFamily() + ", " + entry.getRendererType()
                      + ", " + clazz.getName() + ")");
        }

        try {
            renderKit.addRenderer(entry.getComponentFamily(), entry.getRendererType(), (Renderer) clazz.newInstance());
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        }
    }

    private RenderKitFactory getRenderKitFactory() {
        return (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
    }

    @Override
    protected void addEntity(JavaClass clazz, Map<String, Object> params) {
        String value = getAnnotatedStringParam(params, PAR_FAMILY);
        String theDefault = getAnnotatedStringParam(params, PAR_RENDERERTYPE);

        String renderKitId = getRenderKitId(params);
        RenderKit renderKit = getRenderkit(renderKitId);
        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);
        _alreadyRegistered.put(clazz.getName(), entry);

        if (renderKit == null) {
            log.error("addEntity(): Renderkit with id " + renderKitId + " not found ");
            return;
        }

        if (log.isTraceEnabled()) {
            log.trace("addRenderer(" + renderKitId + ", "
                      + entry.getComponentFamily() + ", " + entry.getRendererType()
                      + ", " + clazz.getFullyQualifiedName() + ")");
        }

        try {
            //recompile the class here because we cannot deal with the renderer otherwise
            renderKit.addRenderer(getAnnotatedStringParam(params,PAR_FAMILY), getAnnotatedStringParam(params, PAR_RENDERERTYPE), (Renderer) ProxyUtils.getWeaver().loadScriptingClassFromName(clazz.getFullyQualifiedName()).newInstance());
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        }
    }

    private String getRenderKitId(Map<String, Object> params) {
        String renderKitId = (String) params.get(PAR_RENDERKITID);
        renderKitId = (renderKitId == null) ? getApplication().getDefaultRenderKitId() : renderKitId;
        return renderKitId;
    }

    private RenderKit getRenderkit(String renderKitId) {
        RenderKitFactory factory = getRenderKitFactory();
        RenderKit renderKit = factory.getRenderKit(FacesContext.getCurrentInstance(), renderKitId);
        return renderKit;
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
        //here the check if the new class is the same as the old one
        return alreadyRegistered.equals(entry);
    }

    @Override
    protected boolean hasToReregister(Map params, JavaClass clazz) {
        String value = getAnnotatedStringParam(params, PAR_FAMILY);
        String theDefault = (String) getAnnotatedStringParam(params, PAR_RENDERERTYPE);
        String renderKitId = (String) getAnnotatedStringParam(params, PAR_RENDERKITID);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getFullyQualifiedName());
        if (alreadyRegistered != null) {
            return !alreadyRegistered.equals(entry);
        }

        return true;
    }

    @Override
    public void purge(String className) {
        super.purge(className);
        AnnotationEntry entry = (AnnotationEntry) _alreadyRegistered.remove(className);
        if (entry == null) {
            return;
        }

        //TODO handle the changed renderer params, but annotation on same
        //class case (remove and add case on the same class)

        RenderKit renderKit = getRenderkit(entry.getRenderKitId());
        try {
            renderKit.addRenderer(entry.getComponentFamily(), entry.getRendererType(), PurgedRenderer.class.newInstance());
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        }
    }
}
