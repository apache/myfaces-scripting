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
package org.apache.myfaces.extensions.scripting.core.reloading;

import org.apache.myfaces.extensions.scripting.core.api.ReloadingStrategy;

/**
 * The managed beans have a different reloading
 * strategy. The dependencies of a managed bean
 * are managed by the IOC container and
 * not transferred over the reloading strategy
 * like for all other artifacts.
 * Hence the bean handler removes the bean and its
 * referring backward dependencies, and the runtime system
 * rebuilds the tree anew.
 *
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ManagedBeanReloadingStrategy implements ReloadingStrategy
{



    public ManagedBeanReloadingStrategy() {
    }

    /**
     * In our case the dropping already has happened at request time
     * no need for another reloading here
     *
     * @param scriptingInstance the instance which has to be reloaded
     * @param artifactType      the type of artifact
     * @return does nothing in this case and returns only the original instance, the reloading is handled
     *         for managed beans on another level
     */
    public Object reload(Object scriptingInstance, int engineType, int artifactType) {
        return scriptingInstance;
    }

}
