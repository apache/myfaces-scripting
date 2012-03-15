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

package org.apache.myfaces.extensions.scripting.jsf.reloading;

import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.Cast;
import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;
import org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy;

import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.ConverterConfig;
import javax.faces.view.facelets.ConverterHandler;

/**
 * The reloading strategy for our converter tag handlers
 * note since we do not have an official api we must
 * enforce a getConverterConfig() method to allow
 * the reloading of converter tag handlers
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@SuppressWarnings("unused")//used dynamically
public class ConverterHandlerReloadingStrategy extends SimpleReloadingStrategy
{

    public ConverterHandlerReloadingStrategy() {
        super();
    }

    @Override
    public Object reload(Object scriptingInstance, int engineType, int artifactType) {
        if (!(scriptingInstance instanceof ComponentHandler)) return scriptingInstance;
        Class aclass = WeavingContext.getInstance().reload(scriptingInstance.getClass());
        if (aclass.hashCode() == scriptingInstance.getClass().hashCode()) {
            //class of this object has not changed although
            // reload is enabled we can skip the rest now
            return scriptingInstance;
        }
        ConverterHandler oldHandler = (ConverterHandler) scriptingInstance;
        /**
         *
         */
        ConverterConfig config = (ConverterConfig) ReflectUtil.executeMethod(oldHandler, "getConverterConfig");
        ConverterHandler newHandler = (ConverterHandler) ReflectUtil.instantiate(aclass, new Cast(ConverterConfig.class, config));

        //save all pending non config related properties wherever possible
        super.mapProperties(newHandler, engineType, oldHandler);

        return newHandler;
    }

}
