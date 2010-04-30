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
package org.apache.myfaces.extensions.scripting.jsf2.annotation;

import org.apache.myfaces.extensions.scripting.api.AnnotationScanListener;
import org.apache.myfaces.extensions.scripting.jsf2.annotation.purged.PurgedRenderer;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class RendererImplementationListener extends MapEntityAnnotationScanner implements AnnotationScanListener {
    private static final String PAR_FAMILY = "componentFamily";
    private static final String PAR_RENDERERTYPE = "rendererType";
    private static final String PAR_RENDERKITID = "renderKitId";

    Map<AnnotationEntry, String> _inverseIndex = new HashMap<AnnotationEntry, String>();

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
                    rendererType.equals(toCompare.getRendererType()) &&
                    renderKitId.equals(toCompare.getRenderKitId());
        }

        @Override
        public int hashCode() {
            /*we calculate the hashcoide to avoid double entries*/
            return (((componentFamily != null) ? componentFamily : "")
                    + "_" +
                    ((rendererType != null) ? rendererType : "")
                    + "_" +
                    ((renderKitId != null) ? renderKitId : "")

            ).hashCode();
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

    public boolean supportsAnnotation(Class annotation) {
        return annotation.equals(FacesRenderer.class);
    }

    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) params.get(PAR_FAMILY);
        String theDefault = (String) params.get(PAR_RENDERERTYPE);

        String renderKitId = getRenderKitId(params);
        RenderKit renderKit = getRenderkit(renderKitId);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault, renderKitId);
        _inverseIndex.put(entry, clazz.getName());
        _alreadyRegistered.put(clazz.getName(), entry);

        if (_log.isLoggable(Level.FINEST)) {
            _log.log(Level.FINEST, "addRenderer(" + renderKitId + ", "
                    + entry.getComponentFamily() + ", " + entry.getRendererType()
                    + ", " + clazz.getName() + ")");
        }

        try {
            renderKit.addRenderer(entry.getComponentFamily(), entry.getRendererType(), (Renderer) clazz.newInstance());
        } catch (InstantiationException e) {
            _log.log(Level.SEVERE, "", e);
        } catch (IllegalAccessException e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    private RenderKitFactory getRenderKitFactory() {
        return (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
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
    public void purge(String className) {
        super.purge(className);
        AnnotationEntry entry = (AnnotationEntry) _alreadyRegistered.remove(className);
        if (entry == null) {
            return;
        }

        RenderKit renderKit = getRenderkit(entry.getRenderKitId());
        try {
            //by fetching the changed renderer we save a full rescan
            String rendererClass = _inverseIndex.get(entry);
            if (rendererClass != null && rendererClass.equals(className)) {
                _inverseIndex.put(entry, PurgedRenderer.class.getName());
                renderKit.addRenderer(entry.getComponentFamily(), entry.getRendererType(), PurgedRenderer.class.newInstance());
            }
        } catch (InstantiationException e) {
            _log.log(Level.SEVERE, "", e);
        } catch (IllegalAccessException e) {
            _log.log(Level.SEVERE, "", e);
        }
    }
}
