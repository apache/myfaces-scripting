package org.apache.myfaces.extensions.scripting.loader.dependencies.registry;

import junit.framework.TestCase;

/**
 * <p>Test class for
 * <code>org.apache.myfaces.extensions.scripting.loader.dependencies.registry.DefaultDependencyRegistry</code>.</p>
 *
 * @author Bernhard Huemer
 */
public class DefaultDependencyRegistryTest extends TestCase {

    // ------------------------------------------ Test methods

    /**
     * <p>Tests whether the registry stores dependencies and dependent classes correctly. Just
     * consider this test case as some kind of example of what I mean with "dependent classes"
     * and "dependencies".</p>
     * 
     */
    public void testRegisterDependencies() {
        DefaultDependencyRegistry registry = new DefaultDependencyRegistry();
        registry.registerDependency("com.foo.Bar", "com.foo.Bla");
        registry.registerDependency("com.foo.Bar", "com.foo.Blubb");

        assertEquals(1, registry.getDependentClasses("com.foo.Bla").size());
        assertTrue(registry.getDependentClasses("com.foo.Bla").contains("com.foo.Bar"));

        assertEquals(1, registry.getDependentClasses("com.foo.Blubb").size());
        assertTrue(registry.getDependentClasses("com.foo.Blubb").contains("com.foo.Bar"));

        assertEquals(0, registry.getDependentClasses("com.foo.Bar").size());
    }

}
