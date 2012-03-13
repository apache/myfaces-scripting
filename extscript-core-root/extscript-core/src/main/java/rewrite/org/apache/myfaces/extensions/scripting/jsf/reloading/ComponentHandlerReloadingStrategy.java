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

package rewrite.org.apache.myfaces.extensions.scripting.jsf.reloading;

import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.Cast;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;
import rewrite.org.apache.myfaces.extensions.scripting.core.context.WeavingContext;
import rewrite.org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ComponentHandlerReloadingStrategy extends SimpleReloadingStrategy
{

    public ComponentHandlerReloadingStrategy() {
        super();
    }

    @Override
    public Object reload(Object scriptingInstance, int artifactType) {
        if (!(scriptingInstance instanceof ComponentHandler)) return scriptingInstance;
        Class aclass = WeavingContext.getInstance().reload(scriptingInstance.getClass());
        if (aclass.hashCode() == scriptingInstance.getClass().hashCode()) {
            //class of this object has not changed although
            // reload is enabled we can skip the rest now
            return scriptingInstance;
        }
        ComponentHandler oldHandler = (ComponentHandler) scriptingInstance;
        ComponentConfig config = oldHandler.getComponentConfig();
        ComponentHandler newHandler = (ComponentHandler) ReflectUtil.instantiate(aclass, new Cast(ComponentConfig.class, config));

        //save all pending non config related properties wherever possible
        super.mapProperties(newHandler, oldHandler);

        return newHandler;
    }

}
