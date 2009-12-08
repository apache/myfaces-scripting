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
package org.apache.myfaces.scripting.core.reloading;

import org.apache.myfaces.scripting.api.ReloadingStrategy;
import org.apache.myfaces.scripting.api.BaseWeaver;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.config.element.ManagedBean;
import org.apache.myfaces.config.RuntimeConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          The managed beans have a different reloading
 *          strategy. We follow the route of dropping
 *          all dynamic beans for now which seems to be a middle ground
 *          between simple (do nothing at all except simple bean reloading)
 *          and graph dependency check (drop only the dependend objects and the
 *          referencing objects)
 */

public class ManagedBeanReloadingStrategy implements ReloadingStrategy {

    BaseWeaver _weaver;
    Map<String, List<ManagedBean>> _managedBeanIdx = null;

    static final String RELOAD_PERFORMED = "beanReloadPerformed";


    public ManagedBeanReloadingStrategy(BaseWeaver weaver) {
        _weaver = weaver;
    }

    public Object reload(Object scriptingInstance, int artefactType) {
        Map requestMap = WeavingContext.getRequestAttributesMap();
        //only one reload per request allowed
        if (requestMap != null && requestMap.containsKey(RELOAD_PERFORMED)) {
            return scriptingInstance;
        }

        //TODO build up the managed bean idx at request time or make a request blocker
        //so that we build up the idx only once per request

        //reload the class to get new static content if needed
        Class aclass = _weaver.reloadScriptingClass(scriptingInstance.getClass());
        if (aclass.hashCode() == scriptingInstance.getClass().hashCode()) {
            //class of this object has not changed although
            // reload is enabled we can skip the rest now
            return scriptingInstance;
        }

        reloadAllDynamicBeans();


        getLog().info("possible reload for " + scriptingInstance.getClass().getName());
        /*only recreation of empty constructor classes is possible*/
        try {
            //reload the object by instiating a new class and
            // assigning the attributes properly
            Object newObject = aclass.newInstance();

            /*now we shuffle the properties between the objects*/
            //TODO remove this we wont need it anymore for now
            mapProperties(newObject, scriptingInstance);

            if (requestMap != null) {
                requestMap.put(RELOAD_PERFORMED, Boolean.TRUE);
            }
            return newObject;
        } catch (Exception e) {
            getLog().error(e);
        }
        return null;

    }

    private void removeBeanReferences(String beanName) {
        getLog().info("ManagedBeanReloadingStrategy.removeBeanReferences(" + beanName + ")");
    }


    /**
     * the simplest solution for now is to dump and reload
     * all managed beans via the config
     * ie - we drop the managed beans from all the request, session
     * and application scope for now by checking the bean maps
     * for their names and removing the corresponding beans unless they implement
     * a non droppable annotation
     */
    private void reloadAllDynamicBeans() {
        //TODO iterate over the bean list, identify which classes are dynamic and drop those
        //via their bean names
        Map<String, ManagedBean> mbeans = RuntimeConfig.getCurrentInstance(FacesContext.getCurrentInstance().getExternalContext()).getManagedBeans();
        for (Map.Entry<String, ManagedBean> entry : mbeans.entrySet()) {
            Class managedBeanClass = entry.getValue().getManagedBeanClass();
            if (WeavingContext.isDynamic(managedBeanClass)) {
                //managed bean class found we drop the class from our session
                removeBeanReferences(entry.getValue().getManagedBeanName());
            }
        }
    }


    /**
     * helper to map the properties wherever possible
     *
     * @param target
     * @param src
     */
    protected void mapProperties(Object target, Object src) {
        try {

            BeanUtils.copyProperties(target, src);
        } catch (IllegalAccessException e) {
            getLog().debug(e);
            //this is wanted
        } catch (InvocationTargetException e) {
            getLog().debug(e);
            //this is wanted
        }
    }

    private final Log getLog() {
        return LogFactory.getLog(this.getClass());
    }
}
