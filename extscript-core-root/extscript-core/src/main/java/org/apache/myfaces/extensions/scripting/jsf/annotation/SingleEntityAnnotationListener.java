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
import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;

import java.lang.annotation.Annotation;

/**
 * annotation scanner which generalized
 * scans annotations with one value entry
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public abstract class SingleEntityAnnotationListener extends BaseAnnotationScanListener implements AnnotationScanListener
{
    String _entityParamValue = null;

    public void register(Class clazz, Annotation annotation) {

        String val = (String) ReflectUtil.executeMethod(annotation, _entityParamValue);
        if (hasToReregister(val, clazz)) {
            addEntity(clazz, val);
        }
    }

    protected abstract void addEntity(Class clazz, String val);

    protected boolean hasToReregister(String name, Class clazz) {
        String componentClass = (String) _alreadyRegistered.get(name);
        return componentClass == null || !componentClass.equals(clazz.getName());
    }

}
