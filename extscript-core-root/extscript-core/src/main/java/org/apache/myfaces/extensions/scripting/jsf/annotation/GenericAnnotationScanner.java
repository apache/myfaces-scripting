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
package org.apache.myfaces.extensions.scripting.jsf.annotation;

import org.apache.myfaces.extensions.scripting.core.api.AnnotationScanListener;
import org.apache.myfaces.extensions.scripting.core.api.ClassScanListener;
import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.engine.api.ClassScanner;
import org.apache.myfaces.extensions.scripting.core.loader.ThrowAwayClassloader;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Source path annotation scanner for java it scans all sources in the specified source paths
 *          recursively for additional information
 *          and then adds the id/name -> class binding information to the correct factory locations,
 *          wherever possible
 */
@SuppressWarnings("unused")
public class GenericAnnotationScanner extends BaseAnnotationScanListener implements ClassScanner
{
    //eventing system not yet fully implemented
    List<ClassScanListener> _listeners = new LinkedList<ClassScanListener>();

    //this registry is needed to keep track of added and moved annotations
    Map<String, String> _registeredAnnotations = new HashMap<String, String>();

    LinkedList<String> _sourcePaths = new LinkedList<String>();

    WeavingContext _weaver = null;

    public GenericAnnotationScanner() {
        _weaver = WeavingContext.getInstance();
        initDefaultListeners();
    }

    public void addScanPath(String sourcePath) {
        _sourcePaths.addFirst(sourcePath);
    }

    Collection<java.lang.annotation.Annotation> filterAnnotations(java.lang.annotation.Annotation[] annotations) {
        List<java.lang.annotation.Annotation> retVal = new ArrayList<java.lang.annotation.Annotation>(annotations.length);
        
        for (java.lang.annotation.Annotation annotation : annotations) {
            if (annotation.annotationType().getName().startsWith(ScriptingConst.JAVAX_FACES)) {
                retVal.add(annotation);
            }

        }
        return retVal;
    }

    public void scanClass(Class clazz) {
        java.lang.annotation.Annotation[] annotations = clazz.getAnnotations();

        Collection<java.lang.annotation.Annotation> annCol = filterAnnotations(annotations);
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
        //for now we only can reach the runtime config in the referenced BaseAnnotatonScanListener
        //if we have a facesContext reachable.
        if (facesContext == null) {
            //TODO (1.1) decouple the scan in the BaseAnnotationScanListener from the facesConfig
            //to get the runtime config
            return;
        }
        if(!_weaver.isPostInit() || _weaver.getLastAnnotationScan() >= _weaver.getLastTaint()) return;
        _weaver.markLastAnnotationScan();


        for (String className : _weaver.loadPossibleDynamicClasses()) {
            try {
                if(!_weaver.isTainted(className)) continue;

                //TODO replace this with a direct call to our weavingContext
                //<>ScannerClassloader loader = new ScannerClassloader(Thread.currentThread().getContextClassLoader(),
                //        -1, null, _weaver.getConfiguration().getCompileTarget());
                ThrowAwayClassloader loader = new ThrowAwayClassloader(ClassUtils.getContextClassLoader(), false);



                Class clazz;
                //in case the class does not exist we have to load it from our weavingcontext
                //otherwise we do just a scan on the class to avoid side behavior
                //if (WeavingContext.getFileChangedDaemon().getClassMap().get(className) == null) {
                //    clazz = _weaver.loadScriptingClassFromName(className);
                //} else {
                    clazz = loader.loadClass(className);
                //}

                if (clazz != null) {
                    java.lang.annotation.Annotation[] anns = clazz.getAnnotations();
                    if (anns != null && anns.length > 0) {
                        addOrMoveAnnotations(clazz);
                    } else {
                        removeAnnotations(clazz);
                    }
                }
            } catch (ClassNotFoundException e) {
                Logger _logger = Logger.getLogger(this.getClass().getName());
                _logger.log(Level.WARNING, "", e);
            }
        }

    }

    /**
     * add or moves a class level annotation
     * to a new place
     *
     * @param clazz the class to have the annotation moved or added
     */
    private void addOrMoveAnnotations(Class clazz) {
        java.lang.annotation.Annotation[] anns = clazz.getAnnotations();
        for (java.lang.annotation.Annotation ann : anns) {
            for (ClassScanListener cListener : _listeners) {
                AnnotationScanListener listener = (AnnotationScanListener) cListener;
                if (listener.supportsAnnotation(ann.annotationType())) {
                    listener.register(clazz, ann);

                    _registeredAnnotations.put(clazz.getName(), ann.annotationType().getName());
                    //TODO check if we still need this
                    //ClassResource metaData = WeavingContext.getInstance().getWatchedResource(clazz.getName());
                    
                }
            }
        }
    }

    /**
     * use case annotation removed
     * we have to entirely remove the annotation
     * from our internal registry and the myfaces registry
     *
     * @param clazz the class to have the annotation removed
     */
    private void removeAnnotations(Class clazz) {
        String registeredAnnotation = _registeredAnnotations.get(clazz.getName());
        if (registeredAnnotation != null) {
            for (ClassScanListener cListener : _listeners) {
                AnnotationScanListener listener = (AnnotationScanListener) cListener;
                if (listener.supportsAnnotation(registeredAnnotation)) {
                    listener.purge(clazz.getName());
                    _registeredAnnotations.remove(clazz.getName());
                    //WeavingContext.getFileChangedDaemon().getClassMap().remove(clazz.getName());
                }
            }
        }
    }

    public void clearListeners() {
        _listeners.clear();
    }

    public void addListener(ClassScanListener listener) {
        _listeners.add(listener);
    }


    public void scanAndMarkChange() {
        //do nothing here
    }
}
