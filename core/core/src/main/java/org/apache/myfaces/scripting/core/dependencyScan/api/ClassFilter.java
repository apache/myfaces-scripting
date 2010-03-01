package org.apache.myfaces.scripting.core.dependencyScan.api;

/**
 * Generic filter pattern interface
 * used by our dependency registry to pre-filter the classes
 */
public interface ClassFilter {

    public boolean isAllowed(Integer engineType, String clazz);
}
