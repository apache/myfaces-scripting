package org.apache.myfaces.scripting.loaders.groovy;

import org.apache.myfaces.groovyloader.core.StandardGroovyReloadingStrategy;
import org.apache.myfaces.scripting.api.*;
import org.apache.myfaces.scripting.core.util.Cast;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.ReflectUtil;
import org.apache.myfaces.extensions.scripting.loaders.groovy.compiler.GroovyCompilerFacade;

import javax.servlet.ServletContext;

/**
 * A standard groovy weaver which isolates the weaving behavior
 */
public class GroovyScriptingWeaver extends BaseWeaver {

    org.apache.myfaces.scripting.loaders.groovy.DynamicClassIdentifier _identifier = new org.apache.myfaces.scripting.loaders.groovy.DynamicClassIdentifier();

    /**
     * helper to allow initial compiler classpath scanning
     *
     * @param servletContext
     */
    public GroovyScriptingWeaver(ServletContext servletContext) {
        super(ScriptingConst.GROOVY_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JSF_GROOVY);
        init();

    }

    public GroovyScriptingWeaver() {
        super(ScriptingConst.FILE_EXTENSION_GROOVY, ScriptingConst.ENGINE_TYPE_JSF_GROOVY);
        init();
    }

    private void init() {
        //init classpath removed we can resolve that over the
        //url classloader at the time myfaces is initialized
        try {
            Class scanner = ClassUtils.getContextClassLoader().loadClass("org.apache.myfaces.scripting.jsf2.annotation.GenericAnnotationScanner");
            this._annotationScanner = (ClassScanner) ReflectUtil.instantiate(scanner, new Cast(ScriptingWeaver.class, this));

        } catch (ClassNotFoundException e) {
            //we do nothing here
        }

        this._dependencyScanner = new GroovyDependencyScanner(this);
        this._reloadingStrategy = new StandardGroovyReloadingStrategy();
        ((StandardGroovyReloadingStrategy) this._reloadingStrategy).setWeaver(this);
    }

    protected String getLoadingInfo(String file) {
        return "[EXT-SCRIPTING] Loading Groovy file:" + file;
    }

    public boolean isDynamic(Class clazz) {
        return _identifier.isDynamic(clazz);  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected DynamicCompiler instantiateCompiler() {
        return new GroovyCompilerFacade();
    }

    /**
     * checks outside of the request scope for changes and taints the corresponding engine
     */
    public void scanForAddedClasses() {
        _dependencyScanner.scanAndMarkChange();
    }

}
