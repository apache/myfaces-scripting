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

import java.util.Map;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public abstract class SingleEntityAnnotationListener extends BaseAnnotationScanListener implements AnnotationScanListener {
    String _entityParamValue = null;

    public void registerSource(Object sourceClass, String annotationName, Map<String, Object> params) {
        JavaClass clazz = (JavaClass) sourceClass;
        String val = getAnnotatedStringParam(params, _entityParamValue);
        if (hasToReregister(val, clazz)) {
            addEntity(clazz, val);
        }
    }

    public void register(Class clazz, String annotationName, Map<String, Object> params) {
        String val = (String) params.get(_entityParamValue);
        if (hasToReregister(val, clazz)) {
            addEntity(clazz, val);
        }
    }


    protected abstract void addEntity(Class clazz, String val);

    protected abstract void addEntity(JavaClass clazz, String val);


    protected boolean hasToReregister(String name, Class clazz) {
        String componentClass = (String) _alreadyRegistered.get(name);
        return componentClass == null || !componentClass.equals(clazz.getName());
    }

    /**
     * simple check we do not check for the contents of the managed property here
     * This is somewhat a simplification does not drag down the managed property handling
     * speed too much
     * <p/>
     * TODO we have to find a way to enable the checking on managed property level
     * so that we can replace the meta data on the fly (probably by extending the interface)
     * for first registration this is enough
     *
     * @param name
     * @param clazz
     * @return
     */
    protected boolean hasToReregister(String name, JavaClass clazz) {
        String componentClass = (String) _alreadyRegistered.get(name);
        return componentClass == null || !componentClass.equals(clazz.getFullyQualifiedName());
    }

}
