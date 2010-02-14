package org.apache.myfaces.extensions.scripting.loader.dependencies.scanner.asm;

import junit.framework.TestCase;
import org.apache.myfaces.extensions.scripting.loader.dependencies.registry.DefaultDependencyRegistry;
import org.apache.myfaces.extensions.scripting.loader.dependencies.registry.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.loader.dependencies.scanner.DependencyScanner;

import java.util.Set;

/**
 *
 */
public class AsmClassReadingDependencyScannerTest extends TestCase {

    // ------------------------------------------ Test methods

    public void testScanDependencies() throws Exception {
        DependencyRegistry registry = new DefaultDependencyRegistry();

        DependencyScanner scanner = new AsmClassReadingDependencyScanner();
        scanner.scan(registry, getClass().getClassLoader(), getClass().getName());

        Set<String> registryDependencies = registry.getDependentClasses(DependencyRegistry.class.getName());
        assertTrue(registryDependencies.size() > 0);
        assertTrue(registryDependencies.contains(getClass().getName()));
    }

}
