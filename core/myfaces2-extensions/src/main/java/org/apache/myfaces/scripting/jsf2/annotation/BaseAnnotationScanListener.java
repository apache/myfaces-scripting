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

import com.thoughtworks.qdox.model.annotation.AnnotationConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.config.RuntimeConfig;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class BaseAnnotationScanListener {
    Log log = LogFactory.getLog(this.getClass());
    static Map<String, Object> _alreadyRegistered = new HashMap<String, Object>();

    protected RuntimeConfig getRuntimeConfig() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        return RuntimeConfig.getCurrentInstance(facesContext.getExternalContext());
    }

    protected Application getApplication() {
        return FacesContext.getCurrentInstance().getApplication();
    }

    protected String getAnnotatedStringParam(Map<String, Object> propMap, String key) {
        AnnotationConstant propVal = (AnnotationConstant) propMap.get(key);
        String val = (String) propVal.getParameterValue();
        val = val.replaceAll("\"", "");
        return val;
    }

    protected Boolean getAnnotatedBolleanParam(Map<String, Object> propMap, String key) {
        AnnotationConstant propVal = (AnnotationConstant) propMap.get(key);
        Boolean val = (Boolean) propVal.getParameterValue();
        return val;
    }

    protected Class getAnnotatedClassParam(Map<String, Object> propMap, String key) {
        AnnotationConstant propVal = (AnnotationConstant) propMap.get(key);
        Class val = (Class) propVal.getParameterValue();
        return val;
    }
}
