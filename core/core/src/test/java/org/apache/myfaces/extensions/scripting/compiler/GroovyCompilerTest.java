package org.apache.myfaces.extensions.scripting.compiler;

import org.apache.myfaces.extensions.scripting.AbstractGeneratorTestCase;

import java.io.File;

/**
 * <p>Test class for
 * <code>org.apache.myfaces.extensions.scripting.compiler.GroovyCompiler</code></p>
 */
public class GroovyCompilerTest extends AbstractGeneratorTestCase {

    // ------------------------------------------ Test methods

    public void testCompileGeneratedFile() throws Exception {
        writeFile("/src/main/groovy/org/apache/myfaces/extensions/scripting/HelloWorld.groovy", new String[]{
                "package org.apache.myfaces.extensions.scripting;   ",
                "                                                   ",
                "def class HelloWorld {                             ",
                "                                                   ",
                "   def static main(String[] args) {                ",
                "       println(\"Hello World\");                   ",
                "   }                                               ",
                "}                                                  "
        });

        Compiler compiler = new GroovyCompiler();
        CompilationResult result = compiler.compile(
                new File(buildAbsolutePath("/src/main/groovy")),
                new File(buildAbsolutePath("/target/test-classes")),
                "org/apache/myfaces/extensions/scripting/HelloWorld.groovy", getCurrentClassLoader());

        assertFalse(result.hasErrors());
        assertTrue(new File(
                buildAbsolutePath("/target/test-classes/"), "org/apache/myfaces/extensions/scripting/HelloWorld.class").exists());
    }

    // ------------------------------------------ Utility methods

    protected ClassLoader getCurrentClassLoader() {
        return getClass().getClassLoader();
    }

}
