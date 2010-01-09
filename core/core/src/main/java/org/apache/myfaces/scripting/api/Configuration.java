package org.apache.myfaces.scripting.api;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Configuration class keeping all the core elements of the configuration
 * this class has to be thread save in its internal data structures!
 * <p/>
 * since the end goal is that a single thread has to preinit the config
 * we don«t have to synchronize its access!
 * <p/>
 */
public class Configuration {

    /**
     * the source dirs per scripting engine
     */
    volatile Map<Integer, CopyOnWriteArrayList<String>> _sourceDirs = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>();

    /**
     * the compile target directories depending on the scripting engine
     */
    volatile Map<Integer, String> _compileTarget = new ConcurrentHashMap<Integer, String>();

    /**
     * we keep track of separate resource dirs
     * for systems which can use resource loaders
     * <p/>
     * so that we can load various resources as well
     * from separate source directories instead
     */
    volatile List<String> _resourceDirs = new CopyOnWriteArrayList<String>();

    public Collection<String> getSourceDirs(int scriptingEngine) {
        return _sourceDirs.get(scriptingEngine);
    }

    public void addSourceDir(int scriptingEngine, String sourceDir) {
        CopyOnWriteArrayList<String> dirs = _sourceDirs.get(scriptingEngine);
        if (dirs == null) {
            dirs = new CopyOnWriteArrayList<String>();
            _sourceDirs.put(scriptingEngine, dirs);
        }
        dirs.add(sourceDir);
    }

    @SuppressWarnings("unused")
    public void addCompileTarget(int scriptingEngine, String target) {
        _compileTarget.put(scriptingEngine, target);
    }

    @SuppressWarnings("unused")
    public String getCompileTarget(int scriptingEngine) {
        return _compileTarget.get(scriptingEngine);
    }

    public void addResourceDir(String dir) {
        _resourceDirs.add(dir);
    }

    public List<String> getResourceDirs() {
        return _resourceDirs;
    }
}
