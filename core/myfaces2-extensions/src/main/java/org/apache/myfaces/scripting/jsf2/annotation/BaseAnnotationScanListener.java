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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class BaseAnnotationScanListener {
    Log log = LogFactory.getLog(this.getClass());
    static Map<String, Object> _alreadyRegistered = new ConcurrentHashMap<String, Object>(8, 0.75f, 1);

    protected RuntimeConfig getRuntimeConfig() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        return RuntimeConfig.getCurrentInstance(facesContext.getExternalContext());
    }

    protected Application getApplication() {
        return FacesContext.getCurrentInstance().getApplication();
    }

    protected String getAnnotatedStringParam(Map<String, Object> propMap, String key) {
        Object tempPropVal = propMap.get(key);
        if(tempPropVal == null) {
            return null;
        }
        if(tempPropVal instanceof String) {
            return (String) tempPropVal;
        }
        AnnotationConstant propVal = (AnnotationConstant) tempPropVal;
      
        String val = (String) propVal.getParameterValue();
        if (val == null) {
            return null;
        }
        val = val.replaceAll("\"", "");
        return val;
    }

    protected Boolean getAnnotatedBolleanParam(Map<String, Object> propMap, String key) {
        AnnotationConstant propVal = (AnnotationConstant) propMap.get(key);
        if(propVal == null) return null;
        Boolean val = (Boolean) propVal.getParameterValue();
        return val;
    }

    protected Class getAnnotatedClassParam(Map<String, Object> propMap, String key) {
        AnnotationConstant propVal = (AnnotationConstant) propMap.get(key);
        if(propVal == null) return null;
        Class val = (Class) propVal.getParameterValue();
        return val;
    }


    /**
     * unregisters this class in the central registry
     * is triggered if the class itself has been registered previously
     *
     * @param className
     * @return
     */
    public void purge(String className) {

    }
}
