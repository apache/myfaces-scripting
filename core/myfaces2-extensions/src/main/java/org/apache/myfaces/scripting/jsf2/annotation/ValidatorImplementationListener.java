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

import javax.faces.validator.FacesValidator;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ValidatorImplementationListener extends MapEntityAnnotationScanner implements AnnotationScanListener {
    private static final String PAR_VALUE = "value";
    private static final String PAR_DEFAULT = "default";

    class AnnotationEntry {
        String value;
        Boolean theDefault;

        AnnotationEntry(String value, Boolean theDefault) {
            this.value = value;
            this.theDefault = theDefault;
        }

        public boolean equals(Object incoming) {
            if (!(incoming instanceof AnnotationEntry)) {
                return false;
            }
            AnnotationEntry toCompare = (AnnotationEntry) incoming;
            //handle null cases
            if ((value == null && toCompare.getValue() != null) ||
                (value != null && toCompare.getValue() == null) ||
                (theDefault == null && toCompare.getTheDefault() != null) ||
                (theDefault != null && toCompare.getTheDefault() == null)) {
                return false;
            } else if (value == null && toCompare.getValue() == null && theDefault == null && toCompare.getTheDefault() == null) {
                return true;
            }

            return value.equals(toCompare.getValue()) && theDefault.equals(toCompare.getValue());
        }

        public String getValue() {
            return value;
        }

        public Boolean getTheDefault() {
            return theDefault;
        }
    }


    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesValidator.class.getName());
    }


    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) params.get(PAR_VALUE);
        Boolean theDefault = (Boolean) params.get(PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault);
        _alreadyRegistered.put(clazz.getName(), entry);

        getApplication().addConverter(entry.getValue(), clazz.getName());
    }

    @Override
    protected void addEntity(JavaClass clazz, Map<String, Object> params) {
        String value = getAnnotatedStringParam(params, PAR_VALUE);
        Boolean theDefault = getAnnotatedBolleanParam(params, PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault);
        _alreadyRegistered.put(clazz.getFullyQualifiedName(), entry);

        getApplication().addConverter(entry.getValue(), clazz.getFullyQualifiedName());
    }

    @Override
    protected boolean hasToReregister(Map params, Class clazz) {
        String value = (String) params.get(PAR_VALUE);
        Boolean theDefault = (Boolean) params.get(PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getName());
        if (alreadyRegistered == null) {
            return true;
        }

        return alreadyRegistered.equals(entry);
    }

    @Override
    protected boolean hasToReregister(Map params, JavaClass clazz) {
        String value = getAnnotatedStringParam(params, PAR_VALUE);
        Boolean theDefault = getAnnotatedBolleanParam(params, PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault);

        AnnotationEntry alreadyRegistered = (AnnotationEntry) _alreadyRegistered.get(clazz.getFullyQualifiedName());
        if (alreadyRegistered == null) {
            return true;
        }

        return alreadyRegistered.equals(entry);
    }
}
