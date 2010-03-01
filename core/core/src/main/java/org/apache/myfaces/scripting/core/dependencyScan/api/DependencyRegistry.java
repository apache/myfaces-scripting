package org.apache.myfaces.scripting.core.dependencyScan.api;

/**
 * General contractual interface for a dependency registry
 * The dependency registry is a class which stores dependencies
 * according to an internal whitelisting system.
 * <p/>
 * Only classes which pass the whitelisting check will be processed
 */
public interface DependencyRegistry {
      /**
     * adds a source dependency if it is able to pass the
     * filters
     * A dependency is only allowed to pass if it is able
     * to pass the internal filter list
     *
     * @param source     the source which includes or casts the dependencies
     * @param dependency the dependency to be added
     */
    void addDependency(Integer engineType, String source, String dependency);
}
