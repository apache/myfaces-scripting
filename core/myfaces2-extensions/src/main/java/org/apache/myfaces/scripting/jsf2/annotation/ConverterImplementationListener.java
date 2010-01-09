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
import org.apache.myfaces.scripting.jsf2.annotation.purged.PurgedConverter;

import javax.faces.application.Application;
import javax.faces.convert.FacesConverter;
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

            if (incoming == null) {
                return false;
            }

            boolean firstEquals = compareValuePair(value, toCompare.getValue());
            boolean secondEquals = compareValuePair(forClass, toCompare.getForClass());

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

        public String getValue() {
            return value;
        }

        public Class getForClass() {
            return forClass;
        }
    }

    public ConverterImplementationListener() {
        super(PAR_VALUE, PAR_DEFAULT);
    }

    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) params.get(PAR_VALUE);
        Class forClass = (Class) params.get(PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, forClass);
        _alreadyRegistered.put(clazz.getName(), entry);

        getApplication().addConverter(entry.getValue(), clazz.getName());
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

        Application application = getApplication();
        application.addConverter(entry.getValue(), PurgedConverter.class.getName());
    }

}
