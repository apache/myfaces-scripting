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

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Annotation;

import java.util.List;
import java.util.LinkedList;
import java.io.File;

import org.apache.myfaces.scripting.api.AnnotationScanner;
import org.apache.myfaces.scripting.api.AnnotationScanListener;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Source path annotation scanner for java it scans all sources in the specified source paths
 *          recursively for additional information
 *          and then adds the id/name -> class binding information to the correct factory locations,
 *          wherever possible
 */

public class SourceAnnotationScanner implements AnnotationScanner {

    List<AnnotationScanListener> _listeners = new LinkedList<AnnotationScanListener>();
    JavaDocBuilder _builder = new JavaDocBuilder();

    public SourceAnnotationScanner(String... sourcePaths) {

        initSourcePaths(sourcePaths);

        initDefaultListeners();

    }


    private void initSourcePaths(String... sourcePaths) {
        for (String sourcePath : sourcePaths) {
            File sourcePathFile = new File(sourcePath);
            if (sourcePathFile.exists()) {
                _builder.addSourceTree(sourcePathFile);
            }
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
        JavaSource[] sources = _builder.getSources();
        for (JavaSource source : sources) {
            String packageName = source.getPackage().toString();
            JavaClass[] classes = source.getClasses();
            for (JavaClass clazz : classes) {
                Annotation[] anns = clazz.getAnnotations();
                for (Annotation ann : anns) {

                    for (AnnotationScanListener listener : _listeners) {
                        if (listener.supportsAnnotation(ann.getClass())) {
                            listener.registerSource(
                                    clazz, ann.getType().getValue(), ann.getPropertyMap());
                        }
                    }
                }
            }
        }
    }

    public void clearListeners() {
        _listeners.clear();
    }

    public void addListener(AnnotationScanListener listener) {
        _listeners.add(listener);
    }

}
