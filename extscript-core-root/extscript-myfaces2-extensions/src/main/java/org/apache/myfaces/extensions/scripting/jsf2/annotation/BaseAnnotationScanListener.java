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
package org.apache.myfaces.extensions.scripting.jsf2.annotation;

import org.apache.myfaces.config.RuntimeConfig;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class BaseAnnotationScanListener {
    Logger log = Logger.getLogger(this.getClass().getName());
    static Map<String, Object> _alreadyRegistered = new ConcurrentHashMap<String, Object>(8, 0.75f, 1);

    protected RuntimeConfig getRuntimeConfig() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        //runtime config not started
        if (facesContext == null) {
            return null;
        }
        return RuntimeConfig.getCurrentInstance(facesContext.getExternalContext());
    }

    protected Application getApplication() {
        return FacesContext.getCurrentInstance().getApplication();
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
