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
package org.apache.myfaces.scripting.refresh;

import org.apache.myfaces.scripting.core.util.WeavingContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * a context which holds information regarding the refresh cycle
 * which can be picked up by the request filter
 * for refreshing strategies
 *
 * That way we can avoid a separate session filter and a push system
 * we use a pull system instead
 */

public class RefreshContext {
    private long personalScopedBeanRefresh = -1l;
    private boolean recompileRecommended = false;

    FileChangedDaemon daemon = FileChangedDaemon.getInstance();

    public long getPersonalScopedBeanRefresh() {
        return personalScopedBeanRefresh;
    }

    public void setPersonalScopedBeanRefresh(long personalScopedBeanRefresh) {
        this.personalScopedBeanRefresh = personalScopedBeanRefresh;
    }

    public boolean isRecompileRecommended(int scriptingEngine) {
        Boolean recommended = daemon.getSystemRecompileMap().get(scriptingEngine);
        return recommended == null || recommended.booleanValue();
    }

    public void setRecompileRecommended(int scriptingEngine, boolean recompileRecommended) {
        daemon.getSystemRecompileMap().put(scriptingEngine, recompileRecommended);
    }

    public FileChangedDaemon getDaemon() {
        return daemon;
    }

    public void setDaemon(FileChangedDaemon daemon) {
        this.daemon = daemon;
    }
}
