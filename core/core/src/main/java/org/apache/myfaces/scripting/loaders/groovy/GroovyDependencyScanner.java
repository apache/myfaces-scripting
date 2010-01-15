package org.apache.myfaces.scripting.loaders.groovy;

import groovy.lang.GroovyClassLoader;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.loaders.java.JavaDependencyScanner;
import org.apache.myfaces.scripting.loaders.java.RecompiledClassLoader;

/**
 *
 */
public class GroovyDependencyScanner extends JavaDependencyScanner {

    public GroovyDependencyScanner(ScriptingWeaver weaver) {
        super(weaver);
    }

    @Override
    protected ClassLoader getClassLoader() {
        return new GroovyRecompiledClassloader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_GROOVY, ScriptingConst.FILE_EXTENSION_GROOVY);
    }

}
