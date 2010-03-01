package org.apache.myfaces.scripting.core.dependencyScan.registry;

import org.apache.myfaces.scripting.core.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.scripting.core.dependencyScan.api.ClassFilter;

/**
 * General contractual interface for a dependency registry with external filters
 * being settable
 * <p/>
 * The dependency registry is a class which stores dependencies
 * according to an internal whitelisting system.
 * <p/>
 * Only classes which pass the whitelisting check will be processed
 */
public interface ExternalFilterDependencyRegistry extends DependencyRegistry {

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
     * Allowance check for external shortcutting
     * This check triggers into the internal filters
     * to pre-check if a class is allowed to pass or not
     *
     * @param className      the classname to be checked
     * @param engineType an identifier for the current scan type (jsf java scan for instance)
     * @return true if it is false otherwise
     */
    public boolean isAllowed(Integer engineType, String className);

    /**
     * Flush operation to batch sync
     * the current dependencies against a storage
     * <p/>
     * (will be removed later once we have all the code transitioned
     * to the registry system)
     */
    void flush(Integer engineType);
}
