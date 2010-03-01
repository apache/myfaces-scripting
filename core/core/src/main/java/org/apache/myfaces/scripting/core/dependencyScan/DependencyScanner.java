package org.apache.myfaces.scripting.core.dependencyScan;

import org.apache.myfaces.scripting.core.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.scripting.core.dependencyScan.registry.ExternalFilterDependencyRegistry;

/**
 * Created by IntelliJ IDEA.
 * User: werpu2
 * Date: 01.03.2010
 * Time: 12:02:20
 * To change this template use File | Settings | File Templates.
 */
public interface DependencyScanner {
    void fetchDependencies(ClassLoader loader, Integer engineType, String className, DependencyRegistry registry);
}
