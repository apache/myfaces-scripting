package org.apache.myfaces.scripting.core.dependencyScan;

/**
 * General contractual interface for a dependency registry
 * The dependency registry is a class which stores dependencies
 * according to an internal whitelisting system.
 * <p/>
 * Only classes which pass the whitelisting check will be processed
 */
public interface DependencyRegistry {

    /**
     * Clears the internal filters
     * for the registry
     */
    void clearFilters();

    /**
     * adds another filter to the internal filter list
     *
     * @param filter the filter to be added
     */
    void addFilter(ClassFilter filter);

    /**
     * adds a source dependency if it is able to pass the
     * filters
     * A dependency is only allowed to pass if it is able
     * to pass the internal filter list
     *
     * @param source     the source which includes or casts the dependencies
     * @param dependency the dependency to be added
     */
    void addDependency(String source, String dependency);

    /**
     * Allowance check for external shortcutting
     * This check triggers into the internal filters
     * to pre-check if a class is allowed to pass or not
     *
     * @param className the classname to be checked
     * @return true if it is false otherwise
     */
    public boolean isAllowed(String className);

    /**
     * Flush operation to batch sync
     * the current dependencies against a storage
     * <p/>
     * (will be removed later once we have all the code transitioned
     * to the registry system)
     */
    void flush();
}
