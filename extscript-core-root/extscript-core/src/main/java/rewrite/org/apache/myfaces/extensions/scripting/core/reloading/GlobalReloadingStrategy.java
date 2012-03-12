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
package rewrite.org.apache.myfaces.extensions.scripting.core.reloading;

import rewrite.org.apache.myfaces.extensions.scripting.core.api.ReloadingStrategy;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import static rewrite.org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A reloading strategy chain of responsibility which switches
 *          depending on the artifact type to the correct
 *          strategy
 *          <p/>
 *          TODO make the reloading strategy pluggable from outside (1.1)!
 */

public class GlobalReloadingStrategy implements ReloadingStrategy
{

    final Logger _logger = Logger.getLogger(GlobalReloadingStrategy.class.getName());

    protected ReloadingStrategy _beanStrategy;
    protected ReloadingStrategy _noMappingStrategy;
    protected ReloadingStrategy _allOthers;

    /*loaded dynamically for myfaces 2+*/
    protected ReloadingStrategy _componentHandlerStrategy;
    protected ReloadingStrategy _validatorHandlerStrategy;
    protected ReloadingStrategy _converterHandlerStrategy;
    protected ReloadingStrategy _behaviorHandlerStrategy;

    public GlobalReloadingStrategy()
    {
        _beanStrategy = new ManagedBeanReloadingStrategy();
        _noMappingStrategy = new NoMappingReloadingStrategy();
        _allOthers = new SimpleReloadingStrategy();

        /*
         * external handlers coming from various submodules
         */
        // _componentHandlerStrategy = dynaload("org.apache.myfaces.extensions.scripting.facelet" +
        //          ".ComponentHandlerReloadingStrategy");
        //  _validatorHandlerStrategy = dynaload("org.apache.myfaces.extensions.scripting.facelet" +
        //          ".ValidatorHandlerReloadingStrategy");
        //  _converterHandlerStrategy = dynaload("org.apache.myfaces.extensions.scripting.facelet" +
        //          ".ConverterHandlerReloadingStrategy");
        //  _behaviorHandlerStrategy = dynaload("org.apache.myfaces.extensions.scripting.facelet" +
        //          ".BehaviorHandlerReloadingStrategy");
    }

    /**
     * the strategy callback which switches between various strategies
     * we have in our system
     *
     * @param toReload     the object which has to be reloaded
     * @param artifactType the artifact type for which the reloading strategy has to be applied to
     * @return either the same or a reloading object depending on the current state of the object
     */
    public Object reload(Object toReload, int artifactType)
    {

        switch (artifactType)
        {
            case ARTIFACT_TYPE_MANAGEDBEAN:
                return _beanStrategy.reload(toReload, artifactType);

            case ARTIFACT_TYPE_RENDERER:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ARTIFACT_TYPE_BEHAVIOR:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ARTIFACT_TYPE_CLIENTBEHAVIORRENDERER:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ARTIFACT_TYPE_COMPONENT:
                return _noMappingStrategy.reload(toReload, artifactType);
            case ARTIFACT_TYPE_VALIDATOR:
                return _noMappingStrategy.reload(toReload, artifactType);

            //    case ARTIFACT_TYPE_COMPONENT_HANDLER:
            //        return dynaReload(toReload, _componentHandlerStrategy, artifactType);
            //    case ARTIFACT_TYPE_CONVERTER_HANDLER:
            //        return dynaReload(toReload, _converterHandlerStrategy, artifactType);
            //    case ARTIFACT_TYPE_VALIDATOR_HANDLER:
            //        return dynaReload(toReload, _validatorHandlerStrategy, artifactType);
            //    case ARTIFACT_TYPE_BEHAVIOR_HANDLER:
            //        return dynaReload(toReload, _behaviorHandlerStrategy, artifactType);

            default:
                return _allOthers.reload(toReload, artifactType);
        }
    }

    public Object dynaReload(Object toReload, ReloadingStrategy strategy, int artifactType)
    {
        if (strategy == null)
        {
            //no strategy no reload
            return toReload;
        } else
        {
            return strategy.reload(toReload, artifactType);
        }
    }

    /**
     * load dynamically the given strategy class
     *
     * @param strategyClass the strategy class which has to be loaded and instantiated
     * @return an instance of the strategy class if found otherwise null
     */
    private ReloadingStrategy dynaload(String strategyClass)
    {
        try
        {
            Class theClass = ClassUtils.forName(strategyClass);
            return (ReloadingStrategy) ReflectUtil.instantiate(theClass);
        }
        catch (RuntimeException ex)
        {
            //in this case swallowing the exception is expected
            if (_logger.isLoggable(Level.FINEST))
            {
                _logger.log(Level.FINEST, "Expected Exception: ", ex);
            }
        }
        return null;
    }

}
