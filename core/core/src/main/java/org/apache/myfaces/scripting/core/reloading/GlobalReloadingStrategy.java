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

    public Object reload(Object toReload) {
        //TODO add the managed bean identification code here
        //so that we can switch strategies on the fly
        return _allOthers.reload(toReload);
    }
}
