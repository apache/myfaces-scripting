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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          a context which holds information regarding the refresh cycle
 *          which can be picked up by the request filter
 *          for refreshing strategies
 *          <p/>
 *          That way we can avoid a separate session filter and a push system
 *          we use a pull system instead
 */

public class RefreshContext {
    /**
     * this is a timed marker which is
     * a point in time the last bean refresh was issued
     * every request has to dump its personal scoped
     * (aka session, or custom) scoped beans
     * if the point in time is newer than the personal refresh time
     * application scoped beans are refreshed at the first refresh cycle
     * by the calling request issuing the compile
     */
    private volatile long personalScopedBeanRefresh = -1l;

    /**
     * the daemon thread which marks the scripting classes
     * depending on the state, changed => tainted == true, not changed
     * tainted == false!
     */
    volatile FileChangedDaemon daemon = FileChangedDaemon.getInstance();

    /*
     * we have to keep the component data as shadow data reachable
     * from various parts of the system to resolve
     * the component <-> renderer dependencies
     * we do not resolve the dependencies between the tag handlers
     * and the components for now
     *
     * resolving those dependencies means
     * renderer changes <-> taint all component classes which use
     * the renderer
     *
     * component changes <-> taint all renderer classes as well
     *
     * This is needed to avoid intra component <-> renderer
     * classcast exceptions
     *
     * the content of this map is the component class
     * and the value is the renderer class
     */
    private volatile Map<String, String> _componentRendererDependencies = new ConcurrentHashMap<String, String>();
    private volatile Map<String, String> _rendererComponentDependencies = new ConcurrentHashMap<String, String>();


    public volatile static Boolean BEAN_SYNC_MONITOR = new Boolean(true);
    public volatile static Boolean RELOAD_SYNC_MONITOR = new Boolean(true);

    public long getPersonalScopedBeanRefresh() {
        return personalScopedBeanRefresh;
    }

    public void setPersonalScopedBeanRefresh(long personalScopedBeanRefresh) {
        this.personalScopedBeanRefresh = personalScopedBeanRefresh;
    }

    /**
     * checks whether it would make sense at the current point
     * in time to enforce a recompile or not
     *
     * @param scriptingEngine
     * @return
     */
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

    /**
     * @return true if a compile currently is in progress
     */
    public static boolean isCompileInProgress(int engineType) {
        //TODO implement this
        return false;
    }

    /**
     * returns whether a recompile now at the current point
     * in time for this engine is allowed or not
     * This state depends on the state of the application
     * if non locked compiles is enabled it always will return true
     * <p/>
     * if a synchronized locking compile is enabled
     * it will return true if the calling request is the only
     * one currently issued because no request is allowed to compile
     * until others have run out
     *
     * @param engineType
     * @return
     */
    public static boolean isComileAllowed(int engineType) {
        //TODO implement synchronized locking logic to avoid
        //race conditions in multiuser environments
        return true;
    }

    public Map<String, String> getComponentRendererDependencies() {
        return _componentRendererDependencies;
    }


    public Map<String, String> getRendererComponentDependencies() {
        return _rendererComponentDependencies;
    }

}
