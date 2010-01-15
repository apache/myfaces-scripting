package org.apache.myfaces.scripting.loaders.groovy;

import groovy.lang.GroovyClassLoader;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Scanning Groovy class loader
 * a groovy classloader which adds dependency scanning
 * as the java compiler part
 *
 * that way we can properly add artefact refreshing
 * to avoid classcast exceptions also on groovy level
 */
public class ScanningGroovyClassloader extends GroovyClassLoader {
    public ScanningGroovyClassloader() {
    }

    public ScanningGroovyClassloader(ClassLoader loader) {
        super(loader);
    }

    public ScanningGroovyClassloader(GroovyClassLoader parent) {
        super(parent);
    }

    public ScanningGroovyClassloader(ClassLoader parent, CompilerConfiguration config, boolean useConfigurationClasspath) {
        super(parent, config, useConfigurationClasspath);
    }

    public ScanningGroovyClassloader(ClassLoader loader, CompilerConfiguration config) {
        super(loader, config);
    }

    /**
        * creates a ClassCollector for a new compilation.
        *
        * @param unit the compilationUnit
        * @param su   the SoruceUnit
        * @return the ClassCollector
        */
       protected ClassCollector createCollector(CompilationUnit unit, SourceUnit su) {
           InnerLoader loader = (InnerLoader) AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                   return new InnerLoader(ScanningGroovyClassloader.this);
               }
           });
           return new MyClassCollector(loader, unit, su);
       }

       public static class MyClassCollector extends ClassCollector {

           public MyClassCollector(InnerLoader cl, CompilationUnit unit, SourceUnit su) {
               super(cl, unit, su);
           }


           protected Class onClassNode(ClassWriter classWriter, ClassNode classNode) {
               byte[] code = classWriter.toByteArray();
               
               //TODO add the scanning code here which changes our metadata and places
               //the dependencies

               return createClass(code, classNode);
           }

       }

}
