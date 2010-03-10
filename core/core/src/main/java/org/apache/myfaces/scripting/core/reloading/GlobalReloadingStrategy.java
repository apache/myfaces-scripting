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
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.Cast;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.ReflectUtil;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A reloading strategy chain of responsibility which switches
 *          depending on the artifact type to the correct
 *          strategy
 *          <p/>
 *          TODO make the reloading strategy pluggable from outside!
 */

public class GlobalReloadingStrategy implements ReloadingStrategy {

    protected ScriptingWeaver _weaver = null;

    protected ReloadingStrategy _beanStrategy;
    protected ReloadingStrategy _noMappingStrategy;
    protected ReloadingStrategy _allOthers;

    /*loaded dynamically for myfaces 2+*/
    protected ReloadingStrategy _componentHandlerStrategy;
    protected ReloadingStrategy _validatorHandlerStrategy;
    protected ReloadingStrategy _converterHandlerStrategy;
    protected ReloadingStrategy _behaviorHandlerStrategy;

    public GlobalReloadingStrategy(ScriptingWeaver weaver) {
        setWeaver(weaver);
    }

    public GlobalReloadingStrategy() {

    }

    /**
     * the strategy callback which switches between various strategies
     * we have in our system
     *
     * @param toReload     the object which has to be reloaded
     * @param artifactType the artifact type for which the reloading strategy has to be applied to
     * @return either the same or a reloading object depending on the current state of the object
     */
    public Object reload(Object toReload, int artifactType) {

        switch (artifactType) {
            case ScriptingConst.ARTIFACT_TYPE_MANAGEDBEAN:
                return _beanStrategy.reload(toReload, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_RENDERER:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_BEHAVIOR:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_CLIENTBEHAVIORRENDERER:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_COMPONENT:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_VALIDATOR:
                return _noMappingStrategy.reload(toReload, artifactType);
            //TODO Add other artifact loading strategies on demand here
            case ScriptingConst.ARTIFACT_TYPE_COMPONENT_HANDLER:
                return dynaReload(toReload, _componentHandlerStrategy, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_CONVERTER_HANDLER:
                return dynaReload(toReload, _converterHandlerStrategy, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_VALIDATOR_HANDLER:
                return dynaReload(toReload, _validatorHandlerStrategy, artifactType);
            case ScriptingConst.ARTIFACT_TYPE_BEHAVIOR_HANDLER:
                return dynaReload(toReload, _behaviorHandlerStrategy, artifactType);

            default:
                return _allOthers.reload(toReload, artifactType);
        }
    }

    public void setWeaver(ScriptingWeaver weaver) {
        _weaver = weaver;
        _beanStrategy = new ManagedBeanReloadingStrategy(weaver);
        _noMappingStrategy = new NoMappingReloadingStrategy(weaver);
        _allOthers = new SimpleReloadingStrategy(weaver);

        /*
         * external handlers coming from various submodules
         */
        _componentHandlerStrategy = dynaload(weaver, "org.apache.myfaces.scripting.facelet.ComponentHandlerReloadingStrategy");
        _validatorHandlerStrategy = dynaload(weaver, "org.apache.myfaces.scripting.facelet.ValidatorHandlerReloadingStrategy");
        _converterHandlerStrategy = dynaload(weaver, "org.apache.myfaces.scripting.facelet.ConverterHandlerReloadingStrategy");
        _behaviorHandlerStrategy = dynaload(weaver, "org.apache.myfaces.scripting.facelet.BehaviorHandlerReloadingStrategy");
    }

    public Object dynaReload(Object toReload, ReloadingStrategy strategy, int artifactType) {
        if (strategy == null) {
            //no strategy no reload
            return toReload;
        } else {
            return strategy.reload(toReload, artifactType);
        }
    }

    /**
     * load dynamically the given strategy class
     *
     * @param weaver        the weaver which the new strateg class is applied to
     * @param strategyClass the strategy class which has to be loaded and instantiated
     * @return an instance of the strategy class if found otherwise null
     */
    private ReloadingStrategy dynaload(ScriptingWeaver weaver, String strategyClass) {
        try {
            Class componentStrategyClass = ClassUtils.forName(strategyClass);
            return (ReloadingStrategy) ReflectUtil.instantiate(componentStrategyClass, new Cast(ScriptingWeaver.class, weaver));
        } catch (RuntimeException ex) {
            //in this case swallowing the exception is expected
        }
        return null;
    }

    public ScriptingWeaver getWeaver() {
        return _weaver;
    }

}
