package org.apache.myfaces.scripting.loaders.groovy;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.java.JavaDependencyScanner;
import org.apache.myfaces.scripting.loaders.java.RecompiledClassLoader;
import org.apache.myfaces.scripting.loaders.java.ScannerClassloader;

/**
 *
 */
public class GroovyDependencyScanner extends JavaDependencyScanner {

    public GroovyDependencyScanner(ScriptingWeaver weaver) {
        super(weaver);
    }

    @Override
    protected ClassLoader getClassLoader() {
        //TODO move the temp dir handling into the configuration
        return new ScannerClassloader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_GROOVY, ScriptingConst.FILE_EXTENSION_GROOVY, WeavingContext.getConfiguration().getCompileTarget());
    }

    @Override
    protected int getEngineType() {
        return ScriptingConst.ENGINE_TYPE_GROOVY;
    }

    @Override
    public void scanPaths() {
        super.scanPaths();    
    }

    protected String getScanIdentifier() {
        return ScriptingConst.ENGINE_TYPE_GROOVY + "_Scan";
    }
}
