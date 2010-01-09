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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.annotation.LifecycleProvider;
import org.apache.myfaces.config.annotation.LifecycleProviderFactory;
import org.apache.myfaces.config.element.ManagedBean;
import org.apache.myfaces.scripting.api.BaseWeaver;
import org.apache.myfaces.scripting.api.ReloadingStrategy;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.ReflectUtil;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import javax.faces.context.FacesContext;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

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

    ScriptingWeaver _weaver;
    Map<String, List<ManagedBean>> _managedBeanIdx = null;

    static final String RELOAD_PERFORMED = "beanReloadPerformed";


    public ManagedBeanReloadingStrategy(ScriptingWeaver weaver) {
        _weaver = weaver;
    }

    public ManagedBeanReloadingStrategy() {
    }

    /**
     * In our case the dropping already has happend at request time
     * no need for another reloading here
     *
     * @param scriptingInstance
     * @param artefactType
     * @return
     */
    public Object reload(Object scriptingInstance, int artefactType) {
        return scriptingInstance;
    }

    public ScriptingWeaver getWeaver() {
        return _weaver;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setWeaver(ScriptingWeaver weaver) {
        _weaver = weaver;
    }

}
