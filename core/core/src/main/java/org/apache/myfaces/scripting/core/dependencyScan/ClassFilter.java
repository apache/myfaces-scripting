package org.apache.myfaces.scripting.core.dependencyScan;

/**
 * Generic filter pattern interface
 * used by our dependency registry to prefilter the classes
 */
public interface ClassFilter {

    public boolean isAllowed(String clazz);
}
