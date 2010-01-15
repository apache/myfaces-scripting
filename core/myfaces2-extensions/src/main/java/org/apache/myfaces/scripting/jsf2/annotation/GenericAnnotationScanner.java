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

import org.apache.myfaces.scripting.api.AnnotationScanListener;
import org.apache.myfaces.scripting.api.ClassScanListener;
import org.apache.myfaces.scripting.api.ClassScanner;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.groovy.GroovyScriptingWeaver;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;

import javax.faces.context.FacesContext;
import java.util.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Source path annotation scanner for java it scans all sources in the specified source paths
 *          recursively for additional information
 *          and then adds the id/name -> class binding information to the correct factory locations,
 *          wherever possible
 */

public class GenericAnnotationScanner extends BaseAnnotationScanListener implements ClassScanner {

    List<ClassScanListener> _listeners = new LinkedList<ClassScanListener>();

    Map<String, String> _registeredAnnotations = new HashMap<String, String>();
    LinkedList<String> _sourcePaths = new LinkedList<String>();
    private static final String JAVAX_FACES = "javax.faces";

    ScriptingWeaver _weaver = null;


    public GenericAnnotationScanner() {
        initDefaultListeners();
    }

    public GenericAnnotationScanner(ScriptingWeaver weaver) {
        _weaver = weaver;
        initDefaultListeners();
    }

    public void addScanPath(String sourcePath) {
        _sourcePaths.addFirst(sourcePath);
    }


    Collection<java.lang.annotation.Annotation> filterAnnotations(java.lang.annotation.Annotation[] anns) {
        List<java.lang.annotation.Annotation> retVal = new ArrayList<java.lang.annotation.Annotation>(anns.length);
        if (anns == null) {
            return retVal;
        }
        for (java.lang.annotation.Annotation ann : anns) {
            if (ann.annotationType().getName().startsWith(JAVAX_FACES)) {
                retVal.add(ann);
            }

        }
        return retVal;
    }

    public void scanClass(Class clazz) {
        java.lang.annotation.Annotation[] anns = clazz.getAnnotations();

        Collection<java.lang.annotation.Annotation> annCol = filterAnnotations(anns);
        if (!annCol.isEmpty()) {
            addOrMoveAnnotations(clazz);
        } else {
            removeAnnotations(clazz);
        }
    }

    private void initDefaultListeners() {
        _listeners.add(new BeanImplementationListener());
        _listeners.add(new BehaviorImplementationListener());
        _listeners.add(new ComponentImplementationListener());
        _listeners.add(new ConverterImplementationListener());
        _listeners.add(new RendererImplementationListener());
        _listeners.add(new ValidatorImplementationListener());
    }

    /**
     * builds up the parsing chain and then notifies its observers
     * on the found data
     */
    public void scanPaths() {
        //https://issues.apache.org/jira/browse/EXTSCRIPT-33

        //check if the faces config is already available otherwise we cannot scan yet
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        //runtime config not started
        //for now we only can reache the runtime config in the referenced BaseAnnotatonScanListener
        //if we have a facesContext reachable.
        if (facesContext == null) {
            //TODO decouple the scan in the BaseAnnotationScanListener from the facesConfig
            //to get the runtime config
            return;
        }

        for (String className : _weaver.loadPossibleDynamicClasses()) {
            Class clazz = _weaver.loadScriptingClassFromName(className);
            //called already by loadScriptingClassFromName


            //TODO unify this, problem is due to the initial class dependency scan
            //we already have the classes but unlike in groovy they are not tainted
            //hence we do not have the annotations yet scanned
            //in groovy we have the initial scan not done hence
            //the annotations are scanned on the fly!
            if (clazz != null && !(_weaver instanceof GroovyScriptingWeaver)) {
                java.lang.annotation.Annotation[] anns = clazz.getAnnotations();
                if (anns != null && anns.length > 0) {
                    addOrMoveAnnotations(clazz);
                } else {
                    removeAnnotations(clazz);
                }
            }
        }

    }


    /**
     * add or moves a class level annotation
     * to a new place
     *
     * @param clazz
     */
    private void addOrMoveAnnotations(Class clazz) {
        java.lang.annotation.Annotation[] anns = clazz.getAnnotations();
        for (java.lang.annotation.Annotation ann : anns) {
            for (ClassScanListener cListener : _listeners) {
                AnnotationScanListener listener = (AnnotationScanListener) cListener;
                if (listener.supportsAnnotation(ann.annotationType().getName())) {
                    listener.register(clazz, ann);

                    _registeredAnnotations.put(clazz.getName(), ann.annotationType().getName());

                    ReloadingMetadata metaData = WeavingContext.getFileChangedDaemon().getClassMap().get(clazz.getName());
                    if (metaData != null) {
                        metaData.setAnnotated(true);
                    }
                }
            }
        }
    }

    /**
     * use case annotation removed
     * we have to entirely remove the annotation
     * from our internal registry and the myfaces registry
     *
     * @param clazz
     */
    private void removeAnnotations(Class clazz) {
        String registeredAnnotation = _registeredAnnotations.get(clazz.getName());
        if (registeredAnnotation != null) {
            for (ClassScanListener cListener : _listeners) {
                AnnotationScanListener listener = (AnnotationScanListener) cListener;
                if (listener.supportsAnnotation(registeredAnnotation)) {
                    listener.purge(clazz.getName());
                    _registeredAnnotations.remove(clazz.getName());
                    WeavingContext.getFileChangedDaemon().getClassMap().remove(clazz.getName());
                }
            }
        }
    }


    private void annotationMoved(Class clazz, java.lang.annotation.Annotation ann, AnnotationScanListener listener) {
        //case class exists but it has been moved to anoter annotation
        String registeredAnnotation = _registeredAnnotations.get(clazz.getName());
        if (registeredAnnotation != null && registeredAnnotation.equals(ann.getClass().getName())) {
            removeAnnotations(clazz);
        }
    }


    public void clearListeners() {
        _listeners.clear();
    }

    public void addListener(ClassScanListener listener) {
        _listeners.add(listener);
    }

}
