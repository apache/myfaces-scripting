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
package rewrite.org.apache.myfaces.extensions.scripting.jsf.annotation;

import rewrite.org.apache.myfaces.extensions.scripting.core.api.AnnotationScanListener;
import rewrite.org.apache.myfaces.extensions.scripting.jsf.annotation.purged.PurgedValidator;

import javax.faces.validator.FacesValidator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ValidatorImplementationListener extends MapEntityAnnotationScanner implements AnnotationScanListener
{

    private static final String PAR_VALUE = "value";
    private static final String PAR_DEFAULT = "isDefault";

    Map<AnnotationEntry, String> _inverseIndex = new HashMap<AnnotationEntry, String>();

    public ValidatorImplementationListener() {
        /*supported annotation parameters rendererType and default*/
        super(PAR_VALUE, PAR_DEFAULT);
    }

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

            if (incoming == null) {
                return false;
            }

            boolean firstEquals = compareValuePair(value, toCompare.getValue());
            boolean secondEquals = compareValuePair(theDefault, toCompare.getTheDefault());

            return firstEquals && secondEquals;
        }

        @Override
        public int hashCode() {
            String retVal = checkForNull(value) + "_" + checkForNull(theDefault);
            return retVal.hashCode();
        }

        private String checkForNull(String in) {
            return (in == null) ? "" : in;
        }

        private String checkForNull(Boolean in) {
            return (in == null) ? "" : String.valueOf(in.booleanValue());
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

        public Boolean getTheDefault() {
            return theDefault;
        }
    }

    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesValidator.class.getName());
    }

    public boolean supportsAnnotation(Class annotation) {
        return annotation.equals(FacesValidator.class);
    }


    @Override
    protected void addEntity(Class clazz, Map<String, Object> params) {
        String value = (String) params.get(PAR_VALUE);
        Boolean theDefault = (Boolean) params.get(PAR_DEFAULT);

        AnnotationEntry entry = new AnnotationEntry(value, theDefault);
        _alreadyRegistered.put(clazz.getName(), entry);
        _inverseIndex.put(entry, clazz.getName());

        getApplication().addValidator(entry.getValue(), clazz.getName());
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
    public void purge(String className) {
        super.purge(className);
        AnnotationEntry entry = (AnnotationEntry) _alreadyRegistered.get(className);
        if (entry == null) {
            return;
        }

        String oldValidator = _inverseIndex.get(entry);
        if (oldValidator.equals(className)) {
            _alreadyRegistered.remove(className);
            getApplication().addValidator(entry.getValue(), PurgedValidator.class.getName());
            _inverseIndex.put(entry, PurgedValidator.class.getName());
        }
    }
}
