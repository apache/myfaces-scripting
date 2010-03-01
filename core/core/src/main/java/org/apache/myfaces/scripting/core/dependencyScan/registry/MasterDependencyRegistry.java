package org.apache.myfaces.scripting.core.dependencyScan.registry;

import org.apache.myfaces.scripting.core.dependencyScan.api.DependencyRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MasterDependencyRegistry implements DependencyRegistry {

    Map<Integer, DependencyRegistry> _subRegistries = new ConcurrentHashMap<Integer, DependencyRegistry>();

    public void addDependency(Integer engineType, String rootClass, String currentClass, String dependency) {
        for (Map.Entry<Integer, DependencyRegistry> entry : _subRegistries.entrySet()) {
            entry.getValue().addDependency(engineType, rootClass, currentClass, dependency);
        }
    }

    public void flush(Integer engineType) {
        for (Map.Entry<Integer, DependencyRegistry> entry : _subRegistries.entrySet()) {
            entry.getValue().flush(engineType);
        }
    }

    public void addSubregistry(Integer engineType, DependencyRegistry registry) {
        _subRegistries.put(engineType, registry);
    }

    public DependencyRegistry getSubregistry(Integer engineType) {
        return _subRegistries.get(engineType);
    }

}
