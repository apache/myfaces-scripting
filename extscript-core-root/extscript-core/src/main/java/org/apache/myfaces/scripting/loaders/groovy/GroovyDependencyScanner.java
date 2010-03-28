package org.apache.myfaces.scripting.loaders.groovy;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.java.JavaDependencyScanner;
import org.apache.myfaces.scripting.loaders.java.ScannerClassloader;

/**
 * Dependency scanner for groovy
 * basically the same as the java dependency scanner
 * but we use a different class here to fulfill
 * our contractual obligations with the chain
 * pattern we use for chaining different scanners
 * depending on the scripting implementation
 */
public class GroovyDependencyScanner extends JavaDependencyScanner {

    public GroovyDependencyScanner(ScriptingWeaver weaver) {
        super(weaver);
    }

    @Override
    protected ClassLoader getClassLoader() {
        //TODO move the temp dir handling into the configuration
        return new ScannerClassloader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_GROOVY, ScriptingConst.FILE_EXTENSION_GROOVY, WeavingContext.getConfiguration().getCompileTarget());
    }

    @Override
    protected int getEngineType() {
        return ScriptingConst.ENGINE_TYPE_JSF_GROOVY;
    }

    @Override
    public void scanPaths() {
        super.scanPaths();
    }

    protected String getScanIdentifier() {
        return ScriptingConst.ENGINE_TYPE_JSF_GROOVY + "_Scan";
    }
}
