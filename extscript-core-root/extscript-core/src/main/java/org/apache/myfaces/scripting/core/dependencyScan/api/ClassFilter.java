package org.apache.myfaces.scripting.core.dependencyScan.api;

/**
 * Generic filter pattern interface
 * used by our dependency registry to pre-filter the classes
 */
public interface ClassFilter {

    /**
     * checks whether the class is allowed to be processed by the filter or not
     *
     * @param engineType integer value of the engine type of the class
     * @param clazz      the class itself to be processed by the filter
     * @return true if it is allowed to be processed false otherwise
     */
    public boolean isAllowed(Integer engineType, String clazz);
}
