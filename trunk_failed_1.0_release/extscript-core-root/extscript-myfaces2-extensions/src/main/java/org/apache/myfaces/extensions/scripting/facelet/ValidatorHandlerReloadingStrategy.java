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

package org.apache.myfaces.extensions.scripting.facelet;

import org.apache.myfaces.extensions.scripting.api.ScriptingWeaver;
import org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy;
import org.apache.myfaces.extensions.scripting.core.util.Cast;
import org.apache.myfaces.extensions.scripting.core.util.ReflectUtil;

import javax.faces.view.facelets.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ValidatorHandlerReloadingStrategy extends SimpleReloadingStrategy {

    public ValidatorHandlerReloadingStrategy(ScriptingWeaver weaver) {
        super(weaver);
    }

    @Override
    public Object reload(Object scriptingInstance, int artifactType) {
        if (!(scriptingInstance instanceof ComponentHandler)) return scriptingInstance;
        Class aclass = _weaver.reloadScriptingClass(scriptingInstance.getClass());
        if (aclass.hashCode() == scriptingInstance.getClass().hashCode()) {
            //class of this object has not changed although
            // reload is enabled we can skip the rest now
            return scriptingInstance;
        }
        ValidatorHandler oldHandler = (ValidatorHandler) scriptingInstance;
        ValidatorConfig config = oldHandler.getValidatorConfig();
        ValidatorHandler newHandler = (ValidatorHandler) ReflectUtil.instantiate(aclass, new Cast(ValidatorConfig.class, config));

        //save all pending non config related properties wherever possible
        super.mapProperties(newHandler, oldHandler);

        return newHandler;
    }

}

