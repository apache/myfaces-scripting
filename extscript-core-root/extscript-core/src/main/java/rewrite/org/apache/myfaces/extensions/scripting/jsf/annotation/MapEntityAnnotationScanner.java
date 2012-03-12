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
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Annotation scanner which scans generically
 * an annotation with more than one entry values.
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public abstract class MapEntityAnnotationScanner extends BaseAnnotationScanListener implements AnnotationScanListener
{

    String[] _annotationParms = null;

    public MapEntityAnnotationScanner(String... annotationParms) {
        _annotationParms = annotationParms;
    }

    public void register(Class clazz, Annotation annotation) {

        Map<String, Object> parms = new HashMap<String, Object>(_annotationParms.length);

        for (String accessor : _annotationParms) {
            parms.put(accessor, ReflectUtil.fastExecuteMethod(annotation, accessor, new Object[0]));
        }

        if (hasToReregister(parms, clazz)) {
            addEntity(clazz, parms);
        }
    }

    protected abstract void addEntity(Class clazz, Map<String, Object> params);

    protected abstract boolean hasToReregister(Map params, Class clazz);

}
