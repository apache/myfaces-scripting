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
import org.apache.myfaces.scripting.jsf2.annotation.purged.PurgedRenderer;
import org.apache.myfaces.scripting.jsf2.annotation.purged.PurgedConverter;

import javax.faces.convert.FacesConverter;
import javax.faces.render.RenderKit;
import javax.faces.application.Application;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ConverterImplementationListener extends MapEntityAnnotationScanner implements AnnotationScanListener {

    private static final String PAR_VALUE = "value";
    private static final String PAR_DEFAULT = "forClass";

    class AnnotationEntry {
        String value;
        Class forClass;

        AnnotationEntry(String value, Class forClass) {
            this.value = value;
            this.forClass = forClass;
        }

        public boolean equals(Object incoming) {
            if (!(incoming instanceof AnnotationEntry)) {
                return false;
            }
            AnnotationEntry toCompare = (AnnotationEntry) incoming;
            //handle null cases
            if ((value == null && toCompare.getValue() != null) ||
                (value != null && toCompare.getValue() == null) ||
                (forClass == null && toCompare.getForClass() != null) ||
                (forClass != null && toCompare.getForClass() == null)) {
                return false;
            } else if (value == null && toCompare.getValue() == null && forClass == null && toCompare.getForClass() == null) {
                return true;
            }

            return value.equals(toCompare.getValue()) && forClass.equals(toCompare.getValue());
        }

        public String getValue() {
            return value;
        }

        public Class getForClass() {
            return forClass;
        }
    }

    public ConverterImplementationListener() {
        super();
    }

    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) params.get(PAR_VALUE);
        Class forClass = (Class) params.get(PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, forClass);
        _alreadyRegistered.put(clazz.getName(), entry);

        getApplication().addValidator(entry.getValue(), clazz.getName());
    }

    @Override
    protected void addEntity(JavaClass clazz, Map<String, Object> params) {
        String value = getAnnotatedStringParam(params, PAR_VALUE);
        Class forClass = getAnnotatedClassParam(params, PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, forClass);
        _alreadyRegistered.put(clazz.getFullyQualifiedName(), entry);

        getApplication().addValidator(entry.getValue(), clazz.getFullyQualifiedName());
    }

    @Override
    protected boolean hasToReregister(Map params, Class clazz) {
        String value = (String) params.get(PAR_VALUE);
        Class forClass = (Class) params.get(PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, forClass);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getName());
        if (alreadyRegistered == null) {
            return true;
        }

        return alreadyRegistered.equals(entry);
    }

    @Override
    protected boolean hasToReregister(Map params, JavaClass clazz) {
        String value = getAnnotatedStringParam(params, PAR_VALUE);
        Class forClass = getAnnotatedClassParam(params, PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, forClass);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getFullyQualifiedName());
        if (alreadyRegistered == null) {
            return true;
        }

        return !alreadyRegistered.equals(entry);
    }

    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesConverter.class.getName());
    }

    @Override
    public void purge(String className) {
        super.purge(className);
        AnnotationEntry entry = (AnnotationEntry) _alreadyRegistered.remove(className);
        if (entry == null) {
            return;
        }

        Application renderKit = getApplication();
        renderKit.addConverter(entry.getValue(), PurgedConverter.class.getName());
    }

}
