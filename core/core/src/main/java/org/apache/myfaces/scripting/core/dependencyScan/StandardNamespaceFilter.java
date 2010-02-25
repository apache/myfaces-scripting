package org.apache.myfaces.scripting.core.dependencyScan;

/**
 * Filter facade for our standard namespace check
 */
public class StandardNamespaceFilter implements ClassFilter {

    public boolean isAllowed(String clazz) {
        return !ClassScanUtils.isStandardNamespace(clazz);
    }
}
