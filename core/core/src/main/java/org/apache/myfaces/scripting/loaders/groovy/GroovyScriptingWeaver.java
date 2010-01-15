package org.apache.myfaces.scripting.loaders.groovy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.api.BaseWeaver;
import org.apache.myfaces.scripting.api.ClassScanner;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.Cast;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.ReflectUtil;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: werpu2
 * Date: 12.01.2010
 * Time: 18:25:27
 * To change this template use File | Settings | File Templates.
 */
public class GroovyScriptingWeaver extends BaseWeaver {

    Log log = LogFactory.getLog(GroovyScriptingWeaver.class);
    String classPath = "";
    org.apache.myfaces.scripting.loaders.groovy.DynamicClassIdentifier identifier = new org.apache.myfaces.scripting.loaders.groovy.DynamicClassIdentifier();

    private static final String GROOVY_FILE_ENDING = ".groovy";

    ClassScanner _annotationScanner = null;
    ClassScanner _dependencyScanner = null;

    org.apache.myfaces.extensions.scripting.loaders.groovy.compiler.GroovyCompilerFacade compiler = null;

    /**
     * helper to allow initial compiler classpath scanning
     *
     * @param servletContext
     */
    public GroovyScriptingWeaver(ServletContext servletContext) {
        super(GROOVY_FILE_ENDING, ScriptingConst.ENGINE_TYPE_GROOVY);
        //init classpath removed we can resolve that over the
        //url classloader at the time myfaces is initialized
        try {
            Class scanner = ClassUtils.getContextClassLoader().loadClass("org.apache.myfaces.scripting.jsf2.annotation.GenericAnnotationScanner");
            this._annotationScanner = (ClassScanner) ReflectUtil.instantiate(scanner, new Cast(ScriptingWeaver.class, this));

        } catch (ClassNotFoundException e) {
            //we do nothing here
        }

        this._dependencyScanner = new GroovyDependencyScanner(this);


    }

    @Override
    public void appendCustomScriptPath(String scriptPath) {
        super.appendCustomScriptPath(scriptPath);
        if (_annotationScanner != null) {
            _annotationScanner.addScanPath(scriptPath);
        }
        _dependencyScanner.addScanPath(scriptPath);
    }

    public GroovyScriptingWeaver() {
        super(ScriptingConst.FILE_EXTENSION_GROOVY, ScriptingConst.ENGINE_TYPE_GROOVY);
    }


    /**
     * loads a class from a given sourceroot and filename
     * note this method does not have to be thread safe
     * it is called in a thread safe manner by the base class
     * <p/>
     * //TODO eliminate the source root we have the roots now somewhere else
     *
     * @param sourceRoot the source search lookup path
     * @param file       the filename to be compiled and loaded
     * @return a valid class if it could be found, null if none was found
     */
    @Override
    protected Class loadScriptingClassFromFile(String sourceRoot, String file) {
        //we load the scripting class from the given className

        File currentClassFile = new File(sourceRoot + File.separator + file);
        if (!currentClassFile.exists()) {
            return null;
        }

        if (log.isInfoEnabled()) {
            log.info("[EXT-SCRIPTING] Loading Groovy file:" + file);
        }

        Iterator<String> it = WeavingContext.getConfiguration().getSourceDirs(getScriptingEngine()).iterator();
        Class retVal = null;

        try {
            //we initialize the compiler lazy
            //because the facade itself is lazy
            if (compiler == null) {
                compiler = new org.apache.myfaces.extensions.scripting.loaders.groovy.compiler.GroovyCompilerFacade();
            }
            retVal = compiler.compileFile(sourceRoot, classPath, file);

            if (retVal == null) {
                return retVal;
            }
        } catch (ClassNotFoundException e) {
            //can be safely ignored
        }


        if (_annotationScanner != null && retVal != null) {
            _annotationScanner.scanClass(retVal);
        }

        return retVal;
    }

   

    public boolean isDynamic(Class clazz) {
        return identifier.isDynamic(clazz);  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * full scan, scans for all artefacts in all files
     */
    public void fullClassScan() {
        _dependencyScanner.scanPaths();


        if (_annotationScanner == null || FacesContext.getCurrentInstance() == null) {
            return;
        }
        _annotationScanner.scanPaths();

    }

    public void fullRecompile() {
        if (isFullyRecompiled()) {
            return;
        }

        if (compiler == null) {
            compiler = new org.apache.myfaces.extensions.scripting.loaders.groovy.compiler.GroovyCompilerFacade();//new ReflectCompilerFacade();
        }

        for (String scriptPath : WeavingContext.getConfiguration().getSourceDirs(getScriptingEngine())) {
            //compile via javac dynamically, also after this block dynamic compilation
            //for the entire length of the request,
            try {
                compiler.compileAllFiles(scriptPath, classPath);
            } catch (ClassNotFoundException e) {
                log.error(e);
            }

        }

        markAsFullyRecompiled();
    }


    private void markAsFullyRecompiled() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            //mark the request as tainted with recompile
            if (context != null) {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                requestMap.put(GroovyScriptingWeaver.class.getName() + "_recompiled", Boolean.TRUE);
            }
        }
        WeavingContext.getRefreshContext().setRecompileRecommended(ScriptingConst.ENGINE_TYPE_GROOVY, Boolean.FALSE);
    }

    private boolean isFullyRecompiled() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            return context.getExternalContext().getRequestMap().containsKey(GroovyScriptingWeaver.class.getName() + "_recompiled");
        }
        return false;
    }
}
