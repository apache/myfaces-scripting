package org.apache.myfaces.extensions.scripting.core.dependencyScan.filter;

import org.apache.myfaces.extensions.scripting.core.dependencyScan.core.ClassScanUtils;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.api.ClassFilter;

/**
 * Filter facade for our standard namespace check
 */
public class StandardNamespaceFilter implements ClassFilter {

    /**
     * is allowed implementation for our standard namespace filter
     *
     * @param engineType integer value of the engine type of the class
     * @param clazz      the class itself to be processed by the filter
     * @return true if it is not in the standard namespaces false otherwise
     */
    public final boolean isAllowed(Integer engineType, String clazz) {
        return !ClassScanUtils.isStandardNamespace(clazz);
    }
}
