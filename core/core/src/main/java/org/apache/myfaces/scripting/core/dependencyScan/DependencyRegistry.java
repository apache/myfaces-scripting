package org.apache.myfaces.scripting.core.dependencyScan;

import org.objectweb.asm.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Registry facade which is used to track our dependencies
 */
public class DependencyRegistry {
    List<ClassFilter> _filters = new LinkedList<ClassFilter>();
    List<String> _dependencies = new LinkedList<String>();

    public DependencyRegistry() {
        _filters.add(new StandardNamespaceFilter());
    }

    public void clearFilters() {
        _filters.clear();
        _filters.add(new StandardNamespaceFilter());
    }

    public void addFilter(ClassFilter filter) {
        _filters.add(filter);
    }

    public void addDependency(String source, String dependency) {
        if (source.equals(dependency)) {
            return;
        }

        if (dependency == null || dependency.trim().equals("")) {
            return;
        }

        for (ClassFilter filter : _filters) {
            if (!filter.isAllowed(dependency)) {
                return;
            }
        }
        //for now we code it into a list like we used to do before
        //but in the long run we have to directly register
        //to save one step

    }

    public List<String> getDependencies() {
        return _dependencies;
    }

}
