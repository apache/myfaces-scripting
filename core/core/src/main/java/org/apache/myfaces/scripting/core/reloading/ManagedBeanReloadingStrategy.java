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
 *          strategy, TODO
 *          we have to figure out which strategy to follow
 */

public class ManagedBeanReloadingStrategy implements ReloadingStrategy {

    BaseWeaver _weaver;
    Map<String, List<ManagedBean>> _managedBeanIdx = null;


    public ManagedBeanReloadingStrategy(BaseWeaver weaver) {
        _weaver = weaver;
        buildupManagedBeanIdx();
    }


    private void buildupManagedBeanIdx() {
        /**
         * we build up a reverse index for the managed beans to resolve
         * make it easier for dependency reloading
         * the current loading strategy is simple
         * normal attribute, we do shift from the old object to the new one
         * managed bean we refresh on the managed bean as well
         * we need the reverse index to identify whether the attribute class justifies for being a
         * managed bean or not
         */
        _managedBeanIdx = new HashMap<String, List<ManagedBean>>();
        Map<String, ManagedBean> beans = RuntimeConfig.getCurrentInstance(FacesContext.getCurrentInstance().getExternalContext()).getManagedBeans();
        for (Map.Entry<String, ManagedBean> entry : beans.entrySet()) {
            String className = entry.getValue().getManagedBeanClassName();
            if (!_managedBeanIdx.containsKey(className)) {
                List<ManagedBean> beanListForClass = new ArrayList<ManagedBean>();
                _managedBeanIdx.put(className, beanListForClass);
                beanListForClass.add(entry.getValue());
            } else {
                _managedBeanIdx.get(className).add(entry.getValue());
            }
        }
    }

    public Object reload(Object scriptingInstance) {
        //TODO build up the managed bean idx at request time or make a request blocker
        //so that we build up the idx only once per request

        //reload the class to get new static content if needed
        Class aclass = _weaver.reloadScriptingClass(scriptingInstance.getClass());
        if (aclass.hashCode() == scriptingInstance.getClass().hashCode()) {
            //class of this object has not changed although
            // reload is enabled we can skip the rest now
            return scriptingInstance;
        }
        getLog().info("possible reload for " + scriptingInstance.getClass().getName());
        /*only recreation of empty constructor classes is possible*/
        try {
            //reload the object by instiating a new class and
            // assigning the attributes properly
            Object newObject = aclass.newInstance();

            /*now we shuffle the properties between the objects*/
            mapProperties(newObject, scriptingInstance);

            return newObject;
        } catch (Exception e) {
            getLog().error(e);
        }
        return null;

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

    protected Log getLog() {
        return LogFactory.getLog(this.getClass());
    }
}
