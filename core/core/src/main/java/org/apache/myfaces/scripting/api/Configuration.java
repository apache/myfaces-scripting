package org.apache.myfaces.scripting.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Configuration class keeping all the core elements of the configuration
 * this class has to be thread save in its internal data structures!
 * <p />
 * since the end goal is that a single thread has to preinit the config
 * we don«t have to synchronize its access!
 * <p />
 */
public class Configuration {

    Map<Integer, CopyOnWriteArrayList<String>> _sourceDirs = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>();
    Map<Integer, String> _compileTarget = new ConcurrentHashMap<Integer, String>();

    public Collection<String> getSourceDirs(int scriptingEngine) {
        return _sourceDirs.get(scriptingEngine);
    }

    public void addSourceDir(int scriptingEngine, String sourceDir) {
        CopyOnWriteArrayList dirs = _sourceDirs.get(scriptingEngine);
        if (dirs == null) {
            dirs = new CopyOnWriteArrayList();
            _sourceDirs.put(scriptingEngine, dirs);
        }
        dirs.add(sourceDir);
    }

    public void addCompileTarget(int scriptingEngine, String target) {
        _compileTarget.put(scriptingEngine, target);
    }

    public String getCompileTarget(int scriptingEngine) {
        return _compileTarget.get(scriptingEngine);
    }
}
