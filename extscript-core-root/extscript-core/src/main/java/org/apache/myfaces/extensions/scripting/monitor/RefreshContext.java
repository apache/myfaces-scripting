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
package org.apache.myfaces.extensions.scripting.monitor;

import org.apache.myfaces.extensions.scripting.core.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.registry.MasterDependencyRegistry;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p/>
 * a context which holds information regarding the refresh cycle
 * which can be picked up by the request filter for refreshing strategies
 * <p/>
 * That way we can avoid a separate session filter and a push system
 * we use a pull system instead
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
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
    private volatile long _personalScopedBeanRefresh = -1l;

    /**
     * the bean synchronisation has to be dealt with
     * differently, we have two volatile points in the lifecycle
     * one being the compile the other one the bean refresh
     * the refresh can only happen outside of a compile cycle
     * and also a global refresh has to be atomic and no other
     * refreshes should happen
     */
    public final static Boolean BEAN_SYNC_MONITOR = new Boolean(true);

    /**
     * second synchronisation monitor
     * all other artifacts can only be refreshed outside of a
     * compile cycle otherwise the classloader would get
     * half finished compile states to load
     */
    public final static Boolean COMPILE_SYNC_MONITOR = new Boolean(true);

    private volatile AtomicInteger _currentlyRunningRequests = null;

    private MasterDependencyRegistry _dependencyRegistry = new MasterDependencyRegistry();

    /**
     * we keep a 10 minutes timeout period to keep the performance in place
     */
    private volatile long _taintLogTimeout = 10 * 60 * 1000;

    /**
     * This is a log which keeps track of the taints
     * over time, we need that mostly for bean refreshes
     * in multiuser surroundings, because only tainted beans need
     * to be refreshed.
     * Now if a user misses multiple updates he has to get a full
     * set of changed classes to be able to drop all personal scoped beans tainted
     * since the he refreshed last! Hence we have to move away from our
     * two dimensional &lt;class, taint&gt; to a three dimensional &lt;class, taint, time&gt;
     * view of things
     */
    private List<TaintingHistoryEntry> _taintLog = Collections.synchronizedList(new LinkedList<TaintingHistoryEntry>());

    /**
     * the daemon thread which marks the scripting classes
     * depending on the state, changed => tainted == true, not changed
     * tainted == false!
     */
    volatile ResourceMonitor _daemon = null;

    /**
     * internal class used by our own history log
     */
    static class TaintingHistoryEntry {
        long _timestamp;
        RefreshAttribute _data;

        public TaintingHistoryEntry(RefreshAttribute data) {
            _data = data.getClone();
            _timestamp = System.currentTimeMillis();
        }

        public long getTimestamp() {
            return _timestamp;
        }

        public RefreshAttribute getData() {
            return _data;
        }
    }

    /**
     * adds a new entry into our taint log
     * which allows us to access tainting data
     * from a given point in time
     *
     * @param data the tainting data to be added
     */
    public void addTaintLogEntry(RefreshAttribute data) {
        _taintLog.add(new TaintingHistoryEntry(data));
    }

    /**
     * garbage collects our tainting data
     * and removes all entries which are not
     * present anymore due to timeout
     * this gc code is called asynchronously
     * from our tainting thread to keep the
     * performance intact
     */
    public void gcTaintLog() {
        long timeoutTimestamp = System.currentTimeMillis() - _taintLogTimeout;
        Iterator<TaintingHistoryEntry> it = _taintLog.iterator();

        while (it.hasNext()) {
            TaintingHistoryEntry entry = it.next();
            if (entry.getTimestamp() < timeoutTimestamp) {
                it.remove();
            }
        }
    }

    /**
     * returns the last noOfEntries entries in the taint history
     *
     * @param noOfEntries the number of entries to be delivered
     * @return a collection of the last &lt;noOfEntries&gt; entries
     */
    public Collection<RefreshAttribute> getLastTainted(int noOfEntries) {
        Iterator<TaintingHistoryEntry> it = _taintLog.subList(Math.max(_taintLog.size() - noOfEntries, 0), _taintLog.size()).iterator();
        List<RefreshAttribute> retVal = new LinkedList<RefreshAttribute>();
        while (it.hasNext()) {
            TaintingHistoryEntry entry = it.next();
            retVal.add(entry.getData());
        }
        return retVal;
    }

    /**
     * Returns a set of tainting data from a given point in time up until now
     *
     * @param timestamp the point in time from which the tainting data has to be derived from
     * @return a set of entries which are a union of all points in time beginning from timestamp
     */
    public Collection<RefreshAttribute> getTaintHistory(long timestamp) {
        List<RefreshAttribute> retVal = new LinkedList<RefreshAttribute>();
        Iterator<TaintingHistoryEntry> it = _taintLog.iterator();

        while (it.hasNext()) {
            TaintingHistoryEntry entry = it.next();
            if (entry.getTimestamp() >= timestamp) {
                retVal.add(entry.getData());
            }
        }
        return retVal;
    }

    /**
     * Returns a set of tainted classes from a given point in time up until now
     *
     * @param timestamp the point in time from which the tainting data has to be derived from
     * @return a set of classnames which are a union of all points in time beginning from timestamp
     */
    public Set<String> getTaintHistoryClasses(long timestamp) {
        Set<String> retVal = new HashSet<String>();
        Iterator<TaintingHistoryEntry> it = _taintLog.iterator();

        while (it.hasNext()) {
            TaintingHistoryEntry entry = it.next();
            if (entry.getTimestamp() >= timestamp) {
                retVal.add(entry.getData().getAClass().getName());
            }
        }
        return retVal;
    }

    /**
     * returns the last global personal scoped bean refresh point in time
     *
     * @return a long value showing which personal bean refresh  was the last in time
     */
    public long getPersonalScopedBeanRefresh() {
        return _personalScopedBeanRefresh;
    }

    /**
     * setter for the global personal scope bean refresh
     *
     * @param personalScopedBeanRefresh
     */
    public void setPersonalScopedBeanRefresh(long personalScopedBeanRefresh) {
        this._personalScopedBeanRefresh = personalScopedBeanRefresh;
    }

    /**
     * checks whether it would make sense at the current point
     * in time to enforce a recompile or not
     *
     * @param scriptingEngine
     * @return
     */
    public boolean isRecompileRecommended(int scriptingEngine) {
        Boolean recommended = _daemon.getSystemRecompileMap().get(scriptingEngine);
        return recommended == null || recommended.booleanValue();
    }

    public void setRecompileRecommended(int scriptingEngine, boolean recompileRecommended) {
        _daemon.getSystemRecompileMap().put(scriptingEngine, recompileRecommended);
    }

    public DependencyRegistry getDependencyRegistry(int scriptingEngine) {
        return _dependencyRegistry.getSubregistry(scriptingEngine);
    }

    public void setDependencyRegistry(int scriptingEngine, DependencyRegistry registry) {
        _dependencyRegistry.addSubregistry(scriptingEngine, registry);
    }

    public boolean isDependencyScanned(int scriptingEngine) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx == null) {
                return false;
            }
            Map<String, Object> requestMap = (Map<String, Object>) ctx.getExternalContext().getRequestMap();
            Boolean retVal = (Boolean) requestMap.get("isDependencyScanned_" + scriptingEngine);
            return (retVal == null) ? false : retVal;
        } catch (UnsupportedOperationException ex) {
            //still in startup
            return false;
        }
    }

    public void setDependencyScanned(int scriptingEngine, Boolean val) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx == null) {
                return;
            }
            Map<String, Object> requestMap = (Map<String, Object>) ctx.getExternalContext().getRequestMap();
            requestMap.put("isDependencyScanned_" + scriptingEngine, val);
        } catch (UnsupportedOperationException ex) {
            //still in startup
        }
    }

    public ResourceMonitor getDaemon() {
        return _daemon;
    }

    public void setDaemon(ResourceMonitor daemon) {
        this._daemon = daemon;
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
    public boolean isComileAllowed(int engineType) {
        return getCurrentlyRunningRequests().get() == 1;
    }

    /**
     * getter for our request counter
     * we need this variable to keep a lock
     * on the number of requests
     * we only can compile if the currently
     * running request is the only one currently
     * active, to keep the compilation results in sync
     *
     * @return the request counter holder which is an atomic integer
     *         <p/>
     *         probably deprecred
     */
    public AtomicInteger getCurrentlyRunningRequests() {
        return _currentlyRunningRequests;
    }

    /**
     * setter for our currently running requests
     *
     * @param currentlyRunning the number of currently running requests
     */
    public void setCurrentlyRunningRequests(AtomicInteger currentlyRunning) {
        _currentlyRunningRequests = currentlyRunning;
    }

    /**
     * checks outside of the request
     * scope for changes and taints
     * the corresponding engine
     */
    public static void scanAndMarkChange() {
        WeavingContext.getWeaver();
    }

    /**
     * Returns our dependency registry
     *
     * @return the Master Dependency registry holding all subregistries
     */
    public MasterDependencyRegistry getDependencyRegistry() {
        return _dependencyRegistry;
    }

    /**
     * Sets our master dependency registry
     *
     * @param dependencyRegistry the master dependency registry to be set
     */
    public void setDependencyRegistry(MasterDependencyRegistry dependencyRegistry) {
        _dependencyRegistry = dependencyRegistry;
    }

    /**
     * getter for the taintlog timeout
     *
     * @return the taintlog timeout
     */
    public long getTaintLogTimeout() {
        return _taintLogTimeout;
    }

    /**
     * setter for the taintlog timeout
     *
     * @param taintLogTimeout a new timeout for the taintlog
     */
    public void setTaintLogTimeout(long taintLogTimeout) {
        _taintLogTimeout = taintLogTimeout;
    }
}
