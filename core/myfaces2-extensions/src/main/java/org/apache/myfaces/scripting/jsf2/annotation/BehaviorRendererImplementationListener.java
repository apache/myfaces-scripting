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
import org.apache.myfaces.scripting.jsf2.annotation.purged.PurgedClientBehaviorRenderer;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesBehaviorRenderer;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Implementation listener for the FacesBehaviorRenderer annotation
 */

public class BehaviorRendererImplementationListener extends MapEntityAnnotationScanner {

    private static final String PAR_RENDERERTYPE = "rendererType";
    private static final String PAR_RENDERKITID = "renderKitId";

    class AnnotationEntry {
        String rendererType;
        String renderKitId;

        AnnotationEntry(String rendererType, String renderKitId) {
            this.rendererType = rendererType;
            this.renderKitId = renderKitId;
        }

        public boolean equals(Object incoming) {
            if (!(incoming instanceof AnnotationEntry)) {
                return false;
            }
            AnnotationEntry toCompare = (AnnotationEntry) incoming;

            if (incoming == null) {
                return false;
            }

            boolean firstEquals = compareValuePair(rendererType, toCompare.getRendererType());
            boolean secondEquals = compareValuePair(renderKitId, toCompare.getRenderKitId());

            return firstEquals && secondEquals;
        }

        protected boolean compareValuePair(Object val1, Object val2) {
            boolean retVal = false;
            if (val1 == null) {
                if (val2 != null) retVal = false;
                if (val2 == null) {
                    retVal = true;
                }
            } else {
                retVal = val1.equals(val2);
            }
            return retVal;
        }

        public String getRendererType() {
            return rendererType;
        }

        public String getRenderKitId() {
            return renderKitId;
        }
    }

    public BehaviorRendererImplementationListener() {
        super();
    }

    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) params.get(PAR_RENDERERTYPE);
        String renderKitId = (String) params.get(PAR_RENDERKITID);

        AnnotationEntry entry = new AnnotationEntry(value, renderKitId);
        _alreadyRegistered.put(clazz.getName(), entry);

        getApplication().addConverter(entry.getRendererType(), clazz.getName());
    }

    @Override
    protected void addEntity(JavaClass clazz, Map<String, Object> params) {
        String value = getAnnotatedStringParam(params, PAR_RENDERERTYPE);
        String renderKitId = getAnnotatedStringParam(params, PAR_RENDERKITID);

        AnnotationEntry entry = new AnnotationEntry(value, renderKitId);
        _alreadyRegistered.put(clazz.getFullyQualifiedName(), entry);

        getApplication().addConverter(entry.getRendererType(), clazz.getFullyQualifiedName());
    }

    @Override
    protected boolean hasToReregister(Map params, Class clazz) {
        String value = (String) params.get(PAR_RENDERERTYPE);
        String renderKitId = (String) params.get(PAR_RENDERKITID);

        AnnotationEntry entry = new AnnotationEntry(value, renderKitId);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getName());
        if (alreadyRegistered == null) {
            return true;
        }

        return alreadyRegistered.equals(entry);
    }

    @Override
    protected boolean hasToReregister(Map params, JavaClass clazz) {
        String value = getAnnotatedStringParam(params, PAR_RENDERERTYPE);
        String renderKitId = getAnnotatedStringParam(params, PAR_RENDERKITID);

        AnnotationEntry entry = new AnnotationEntry(value, renderKitId);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getFullyQualifiedName());
        if (alreadyRegistered == null) {
            return true;
        }

        return !alreadyRegistered.equals(entry);
    }

    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesBehaviorRenderer.class.getName());
    }


    private RenderKitFactory getRenderKitFactory() {
        return (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
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
            renderKit.addClientBehaviorRenderer(entry.getRendererType(), PurgedClientBehaviorRenderer.class.newInstance());
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        }
    }

}
