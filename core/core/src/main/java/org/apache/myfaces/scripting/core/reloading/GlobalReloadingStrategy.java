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

import org.apache.myfaces.scripting.api.BaseWeaver;
import org.apache.myfaces.scripting.api.ReloadingStrategy;
import org.apache.myfaces.scripting.api.ScriptingConst;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A reloading strategy chain of responsibility which switches
 *          depending on the artefact type to the correct
 *          strategy
 */

public class GlobalReloadingStrategy implements ReloadingStrategy {

    private BaseWeaver _weaver = null;

    private ReloadingStrategy _beanStrategy;
    private ReloadingStrategy _allOthers;

    public GlobalReloadingStrategy(BaseWeaver weaver) {
        _weaver = weaver;
        _beanStrategy = new ManagedBeanReloadingStrategy(weaver);
        _allOthers = new SimpleReloadingStrategy(weaver);
    }

    /**
     * the strategy callback which switches between various strategies
     * we have in our system
     *
     * @param toReload
     * @param artefactType
     * @return
     */
    public Object reload(Object toReload, int artefactType) {

        switch (artefactType) {
            case ScriptingConst.ARTEFACT_TYPE_MANAGEDBEAN:
                return _beanStrategy.reload(toReload, artefactType);
            //TODO Add other artefact loading strategies on demand here
            default:
                return _allOthers.reload(toReload, artefactType);
        }
    }
}
