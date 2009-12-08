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
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A simple implementation of our reloading strategy
 *          pattern this is the most basic implementation
 *          covering our reloading.
 *          <p/>
 *          Applicable for most artefacts except for now managed beans
 *          <p/>
 */

public class SimpleReloadingStrategy implements ReloadingStrategy {

    BaseWeaver _weaver;

    public SimpleReloadingStrategy(BaseWeaver weaver) {
        _weaver = weaver;
    }

    /**
     * <p>
     * the central callback for our strategy here
     * it has to handle the reload of the scriptingInstance
     * if possible, otherwise it has to return the
     * original object if no reload was necessary or possible
     * </p>
     *
     * @param scriptingInstance the instance to be reloaded by the system
     * @return either the same object or a new instance utilizing the changed code
     */
    public Object reload(Object scriptingInstance, int artefactType) {

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
     * <p/>
     * This is the simplest solution for now,
     * we apply only a copy properties here, which should be enough
     * for all artefacts except the managed beans and the ones
     * which have to preserve some kind of delegate before instantiation.
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
