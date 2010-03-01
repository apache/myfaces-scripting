package org.apache.myfaces.scripting.core.dependencyScan.registry;

import org.apache.myfaces.scripting.core.dependencyScan.api.ClassFilter;
import org.apache.myfaces.scripting.core.dependencyScan.filter.StandardNamespaceFilter;
import org.apache.myfaces.scripting.core.util.Strategy;

import java.util.*;

/**
 * registry facade which is used to track our dependencies
 */
public class DependencyRegistryImpl implements ExternalFilterDependencyRegistry {
    List<ClassFilter> _filters = new LinkedList<ClassFilter>();

    Map<String, Set<String>> _dependencies = new HashMap<String, Set<String>>();

    Strategy _dependencyTarget = null;

    public DependencyRegistryImpl(Strategy dependencyTarget) {
        _filters.add(new StandardNamespaceFilter());
        _dependencyTarget = dependencyTarget;
    }

    public void clearFilters() {
        _filters.clear();
        _filters.add(new StandardNamespaceFilter());
    }

    public void addFilter(ClassFilter filter) {
        _filters.add(filter);
    }

    public boolean isAllowed(String scanIdentifier, String className) {
        for (ClassFilter filter : _filters) {
            if (!filter.isAllowed(className)) {
                return false;
            }
        }
        return true;
    }

    public void addDependency(String scanIdentifier, String source, String dependency) {
        if (source.equals(dependency)) {
            return;
        }

        if (dependency == null || dependency.trim().equals("")) {
            return;
        }

        if (!isAllowed(scanIdentifier, dependency)) {
            return;
        }
        //for now we code it into a list like we used to do before
        //but in the long run we have to directly register
        //to save one step
        getDependencySet(source).add(dependency);
    }

    private Set<String> getDependencySet(String key) {
        Set<String> retVal = _dependencies.get(key);
        if (retVal == null) {
            retVal = new HashSet<String>();
            _dependencies.put(key, retVal);
        }
        return retVal;
    }

    public Map<String, Set<String>> getDependencies() {
        return _dependencies;
    }

    /**
     * flush to flush down our stored dependencies into our final map
     */
    public void flush(String scanIdentifier) {
        _dependencyTarget.apply(_dependencies);
    }

}
