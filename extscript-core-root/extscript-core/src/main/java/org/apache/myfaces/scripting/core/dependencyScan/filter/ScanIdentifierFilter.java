package org.apache.myfaces.scripting.core.dependencyScan.filter;

import org.apache.myfaces.scripting.core.dependencyScan.api.ClassFilter;

import java.util.Arrays;

/**
 * a filter which works on the scan identifiers
 * only classes which trigger on the same identifier
 * are allowed to be passed through
 */
public class ScanIdentifierFilter implements ClassFilter {

    private final int [] _engineType;

    public ScanIdentifierFilter(int ... engineType) {
        _engineType = Arrays.copyOf(engineType, engineType.length);
    }

    public boolean isAllowed(Integer identifier, String clazz) {
        int id = identifier.intValue();
        for(int engineType: _engineType) {
            boolean allowed = engineType == id;
            if(allowed) return true;
        }
        return false;
    }
}
