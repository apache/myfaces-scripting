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

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.api.ScriptingWeaver;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.core.ClassDependencies;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.extensions.scripting.api.extensionevents.ClassTaintedEvent;

import javax.servlet.ServletContext;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central daemon thread which watches the resources
 * for changes and marks them as changed.
 * This watchdog daemon is the central core
 * of the entire scripting engine it runs asynchronously
 * to your program and keeps an eye on the resources
 * and their dependencies, once a file has changed
 * all the referring dependencies are also marked
 * as being to reloaded.
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class FileChangedDaemon extends Thread {

    private static final String CONTEXT_KEY = "extscriptDaemon";

    static FileChangedDaemon _instance = null;

    Map<String, ReloadingMetadata> _classMap = new ConcurrentHashMap<String, ReloadingMetadata>(8, 0.75f, 1);
    ClassDependencies _dependencyMap = new ClassDependencies();

    /**
     * This map is a shortcut for the various scripting engines.
     * It keeps track whether the engines source paths
     * have dirty files or not and if true we enforce a recompile at the
     * next refresh!
     * <p/>
     * We keep track on engine level to avoid to search the classMap for every refresh
     * the classMap still is needed for various identification tasks which are reload
     * related
     */
    Map<Integer, Boolean> _systemRecompileMap = new ConcurrentHashMap<Integer, Boolean>(8, 0.75f, 1);

    boolean _running = false;
    boolean _contextInitialized = false;
    Logger _log = Logger.getLogger(FileChangedDaemon.class.getName());
    ScriptingWeaver _weavers = null;
    static WeakReference<ServletContext> _externalContext;

    public static synchronized void startup(ServletContext externalContext) {
        if (_externalContext != null) return;
        _externalContext = new WeakReference<ServletContext>(externalContext);

        //we currently keep it as singleton but in the long run we will move it into the context
        //like everything else singleton-wise
        if (WeavingContext.isScriptingEnabled() && _instance == null) {
            _instance = new FileChangedDaemon();
            externalContext.setAttribute(CONTEXT_KEY, _instance);
            /**
             * daemon thread to allow forced
             * shutdowns for web context restarts
             */
            _instance.setDaemon(true);
            _instance.setRunning(true);
            _instance.start();

        }

    }

    public static synchronized void clear() {

    }

    public static synchronized FileChangedDaemon getInstance() {
        //we do it in this complicated manner because of find bugs
        //practically this cannot really happen except for shutdown were it is not important anymore
        ServletContext context = _externalContext.get();
        if (context != null) {
           return (FileChangedDaemon) context.getAttribute(CONTEXT_KEY);
        }
        return null;
        //return (FileChangedDaemon) ((ServletContext) _externalContext.get()).getAttribute(CONTEXT_KEY);
    }

    /**
     * Central run method
     * which performs the entire scanning process
     */
    public void run() {
        while (WeavingContext.isScriptingEnabled() && _running) {
            if (_externalContext != null && _externalContext.get() != null && !_contextInitialized) {
                WeavingContext.initThread((ServletContext) _externalContext.get());
                _contextInitialized = true;
            }
            try {
                try {
                    Thread.sleep(ScriptingConst.TAINT_INTERVAL);
                } catch (InterruptedException e) {
                    //if the server shuts down while we are in sleep we get an error
                    //which we better should swallow
                }

                if (_classMap == null || _classMap.size() == 0)
                    continue;
                if (_contextInitialized)
                    checkForChanges();
            } catch (Throwable e) {
                _log.log(Level.SEVERE, "[EXT-SCRIPTING]", e);

            }
        }
        if (_log.isLoggable(Level.INFO)) {
            _log.info("[EXT-SCRIPTING] Dynamic reloading watch daemon is shutting down");
        }
    }

    /**
     * central tainted mark method which keeps
     * track if some file in one of the supported engines has changed
     * and if yes marks the file as tainted as well
     * as marks the engine as having to do a full recompile
     */
    private final void checkForChanges() {
        ScriptingWeaver weaver = WeavingContext.getWeaver();
        if (weaver == null) return;
        weaver.scanForAddedClasses();

        for (Map.Entry<String, ReloadingMetadata> it : this._classMap.entrySet()) {

            File proxyFile = new File(it.getValue().getSourcePath() + File.separator + it.getValue().getFileName());
            if (isModified(it, proxyFile)) {

                _systemRecompileMap.put(it.getValue().getScriptingEngine(), Boolean.TRUE);
                ReloadingMetadata meta = it.getValue();
                meta.setTainted(true);
                meta.setTaintedOnce(true);
                printInfo(it, proxyFile);
                meta.setTimestamp(proxyFile.lastModified());
                dependencyTainted(meta.getAClass().getName());

                //we add our log entry for further reference
                WeavingContext.getRefreshContext().addTaintLogEntry(meta);
                WeavingContext.getExtensionEventRegistry().sendEvent(new ClassTaintedEvent(meta));
            }
            //}
        }
        //we clean up the taint log
        WeavingContext.getRefreshContext().gcTaintLog();
    }

    /**
     * recursive walk over our meta data to taint also the classes
     * which refer to our refreshing class so that those
     * are reloaded as well, this helps to avoid classcast
     * exceptions caused by imports and casts on long running artifacts
     *
     * @param className the origin classname which needs to be walked recursively
     */
    private void dependencyTainted(String className) {
        Set<String> referrers = _dependencyMap.getReferringClasses(className);
        if (referrers == null) return;
        for (String referrer : referrers) {
            ReloadingMetadata metaData = _classMap.get(referrer);
            if (metaData == null) continue;
            if (metaData.isTainted()) continue;
            printInfo(metaData);

            metaData.setTainted(true);
            metaData.setTaintedOnce(true);
            dependencyTainted(metaData.getAClass().getName());
            WeavingContext.getRefreshContext().addTaintLogEntry(metaData);
            WeavingContext.getExtensionEventRegistry().sendEvent(new ClassTaintedEvent(metaData));
        }
    }

    private final boolean isModified(Map.Entry<String, ReloadingMetadata> it, File proxyFile) {
        return proxyFile.lastModified() != it.getValue().getTimestamp();
    }

    private void printInfo(ReloadingMetadata it) {
        if (_log.isLoggable(Level.INFO)) {
            _log.log(Level.INFO, "[EXT-SCRIPTING] Tainting Dependency: {0}", it.getFileName());
        }
    }

    private void printInfo(Map.Entry<String, ReloadingMetadata> it, File proxyFile) {
        if (_log.isLoggable(Level.INFO)) {
            _log.log(Level.INFO, "[EXT-SCRIPTING] comparing {0} Dates: {1} {2} ", new String[]{it.getKey(), Long.toString(proxyFile.lastModified()), Long.toString(it.getValue().getTimestamp())});
            _log.log(Level.INFO, "[EXT-SCRIPTING] Tainting: {0}", it.getValue().getFileName());
        }
    }

    public boolean isRunning() {
        return _running;
    }

    public void setRunning(boolean running) {
        this._running = running;
    }

    public Map<Integer, Boolean> getSystemRecompileMap() {
        return _systemRecompileMap;
    }

    public void setSystemRecompileMap(Map<Integer, Boolean> systemRecompileMap) {
        this._systemRecompileMap = systemRecompileMap;
    }

    public Map<String, ReloadingMetadata> getClassMap() {
        return _classMap;
    }

    public void setClassMap(Map<String, ReloadingMetadata> classMap) {
        this._classMap = classMap;
    }

    public ScriptingWeaver getWeavers() {
        return _weavers;
    }

    public void setWeavers(ScriptingWeaver weavers) {
        _weavers = weavers;
    }

    public ClassDependencies getDependencyMap() {
        return _dependencyMap;
    }
}

