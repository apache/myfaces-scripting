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

import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.impl.digester.elements.ManagedBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.application.Application;
import java.util.Map;
import java.util.HashMap;

import com.thoughtworks.qdox.model.annotation.AnnotationConstant;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class BaseAnnotationScanListener {
    Log log = LogFactory.getLog(this.getClass());
    static Map<String, ManagedBean> _alreadyRegistered = new HashMap<String, ManagedBean>();

    protected RuntimeConfig getRuntimeConfig() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        return RuntimeConfig.getCurrentInstance(facesContext.getExternalContext());
    }

    protected Application getApplication() {
        return FacesContext.getCurrentInstance().getApplication();
    }

    protected String getAnnotatedStringParam(Map<String, Object> propMap, String key) {
        AnnotationConstant propVal = (AnnotationConstant) propMap.get(key);
        String name = (String) propVal.getParameterValue();
        name = name.replaceAll("\"", "");
        return name;
    }

    protected boolean hasToReregister(String name, Class clazz) {
        ManagedBean mbean = _alreadyRegistered.get(name);
        return mbean == null || !mbean.getManagedBeanClassName().equals(clazz.getName());
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
        ManagedBean mbean = _alreadyRegistered.get(name);
        return mbean == null || !mbean.getManagedBeanClassName().equals(clazz.getFullyQualifiedName());
    }
}
