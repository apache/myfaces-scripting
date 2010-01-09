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

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A reloading strategy chain of responsibility which switches
 *          depending on the artifact type to the correct
 *          strategy
 */

public class GlobalReloadingStrategy implements ReloadingStrategy {

    protected ScriptingWeaver _weaver = null;

    protected ReloadingStrategy _beanStrategy;
    protected ReloadingStrategy _noMappingStrategy;
    protected ReloadingStrategy _allOthers;

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
            default:
                return _allOthers.reload(toReload, artifactType);
        }
    }

    public void setWeaver(ScriptingWeaver weaver) {
        _weaver = weaver;
        _beanStrategy = new ManagedBeanReloadingStrategy(weaver);
        _noMappingStrategy = new NoMappingReloadingStrategy(weaver);
        _allOthers = new SimpleReloadingStrategy(weaver);
    }

    public ScriptingWeaver getWeaver() {
        return _weaver;
    }

}
