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
     * @param engineType            the engine type for this dependency
     * @param rootClass             the root class of this scan which all dependencies are referenced from
     * @param currentlyVisitedClass the source which includes or casts the dependencies
     * @param dependency            the dependency to be added
     */
    void addDependency(Integer engineType, String rootClass, String currentlyVisitedClass, String dependency);

    /**
     * Flush which is issued at the end of processing to flush
     * any content which has not been yet processed into our content holding
     * data structures
     *
     * @param engineType the engine type which has issued the flush operation
     */
    void flush(Integer engineType);
}
