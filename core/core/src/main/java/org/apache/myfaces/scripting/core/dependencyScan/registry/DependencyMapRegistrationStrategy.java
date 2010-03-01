package org.apache.myfaces.scripting.core.dependencyScan.registry;

import org.apache.myfaces.scripting.core.dependencyScan.core.ClassDependencies;
import org.apache.myfaces.scripting.core.util.Strategy;

import java.util.Map;
import java.util.Set;

/**
 * Strategy implementation
 * which transitions our dependencies
 * into our "legacy" dependency map
 */
public class DependencyMapRegistrationStrategy implements Strategy {
    ClassDependencies _dependencyMap = new ClassDependencies();
    String _rootClass;

    public DependencyMapRegistrationStrategy(String rootClass, ClassDependencies dependencyMap) {
        _dependencyMap = dependencyMap;
        _rootClass = rootClass;
    }

    public void apply(Object element) {

        Map<String, Set<String>> workMap = (Map<String, Set<String>>) element;
        for (Map.Entry<String, Set<String>> entry : workMap.entrySet()) {
            _dependencyMap.addDependencies(_rootClass, entry.getValue());
        }
    }

    public ClassDependencies getDependencyMap() {
        return _dependencyMap;
    }

    public void setDependencyMap(ClassDependencies dependencyMap) {
        _dependencyMap = dependencyMap;
    }

    public String getRootClass() {
        return _rootClass;
    }

    public void setRootClass(String rootClass) {
        _rootClass = rootClass;
    }
}
