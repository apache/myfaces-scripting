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
        //TODO return the groovy classloader here
        //which has to serve the groovy resources

        GroovyClassLoader gcl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        for (String sourceDir : WeavingContext.getConfiguration().getSourceDirs(ScriptingConst.ENGINE_TYPE_GROOVY)) {
            gcl.addClasspath(sourceDir);
        }
        return gcl;
    }
}
