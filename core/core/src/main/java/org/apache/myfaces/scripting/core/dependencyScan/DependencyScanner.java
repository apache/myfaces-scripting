package org.apache.myfaces.scripting.core.dependencyScan;

import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A dependency scanner for
 *          our classes
 *          <p/>
 *          The idea beind it is that a dependency scanner
 *          should scan loaded classes for their dependencies
 *          into a whitelist of packages, a dynamically loaded class
 *          then now can taint other classes if altered
 *          which are in the whitelist so that those artefacts get reloaded
 *          <p/>
 *          The whitelist itself for now should only be
 *          classes from dynamically loaded packages
 */
public interface DependencyScanner {
    public Set<String> fetchDependencies(String className, Set<String> whiteList);
}
