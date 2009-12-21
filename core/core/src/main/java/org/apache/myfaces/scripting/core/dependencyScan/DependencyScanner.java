package org.apache.myfaces.scripting.core.dependencyScan;

import java.util.Set;

/**

 */
public interface DependencyScanner {
    public Set<String> fetchDependencies(String className);
}
