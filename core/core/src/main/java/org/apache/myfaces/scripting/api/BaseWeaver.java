package org.apache.myfaces.scripting.api;

import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;
import org.apache.myfaces.scripting.refresh.FileChangedDaemon;
import org.apache.myfaces.scripting.core.reloading.SimpleReloadingStrategy;
import org.apache.myfaces.scripting.core.reloading.GlobalReloadingStrategy;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.core.util.ReflectUtil;
import org.apache.myfaces.config.element.ManagedBean;
import org.apache.myfaces.config.RuntimeConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import java.util.*;
import java.io.File;

/**
 * @author Werner Punz
 *         <p/>
 *         Refactored the common weaver code into a base class
 */
public abstract class BaseWeaver implements ScriptingWeaver {

    private String fileEnding = null;
    private int scriptingEngine = ScriptingConst.ENGINE_TYPE_NO_ENGINE;
    /**
     * only be set from the
     * initialisation code so no thread safety needed
     */
    protected List<String> scriptPaths = new LinkedList<String>();

    protected ReloadingStrategy _reloadingStrategy = null;


    public BaseWeaver() {
        _reloadingStrategy = new GlobalReloadingStrategy(this);
    }

    public BaseWeaver(String fileEnding, int scriptingEngine) {
        this.fileEnding = fileEnding;
        this.scriptingEngine = scriptingEngine;
        _reloadingStrategy = new GlobalReloadingStrategy(this);
    }


    /**
     * add custom source lookup paths
     *
     * @param scriptPath the new path which has to be added
     */
    public void appendCustomScriptPath(String scriptPath) {
        if (scriptPath.endsWith("/") || scriptPath.endsWith("\\/")) {
            scriptPath = scriptPath.substring(0, scriptPath.length() - 1);
        }

        getScriptPaths().add(scriptPath);
    }


    /**
     * condition which marks a metadata as reload candidate
     */
    public boolean isReloadCandidate(ReloadingMetadata reloadMeta) {
        return reloadMeta != null && assertScriptingEngine(reloadMeta) && reloadMeta.isTaintedOnce();
    }

    /**
     * helper for accessing the reloading metadata map
     *
     * @return
     */
    protected Map<String, ReloadingMetadata> getClassMap() {
        return FileChangedDaemon.getInstance().getClassMap();
    }

    /**
     * reloads a scripting instance object
     *
     * @param scriptingInstance the object which has to be reloaded
     * @return the reloaded object with all properties transferred or the original object if no reloading was needed
     */
    public Object reloadScriptingInstance(Object scriptingInstance, int artefactType) {
        Map<String, ReloadingMetadata> classMap = getClassMap();
        if (classMap.size() == 0) {
            return scriptingInstance;
        }

        ReloadingMetadata reloadMeta = classMap.get(scriptingInstance.getClass().getName());

        //This gives a minor speedup because we jump out as soon as possible
        //files never changed do not even have to be considered
        //not tained even once == not even considered to be reloaded
        if (isReloadCandidate(reloadMeta)) {

            Object reloaded = _reloadingStrategy.reload(scriptingInstance, artefactType);
            if (reloaded != null) {
                return reloaded;
            }

        }
        return scriptingInstance;

    }

    /**
     * reweaving of an existing woven class
     * by reloading its file contents and then reweaving it
     */
    public Class reloadScriptingClass(Class aclass) {
        ReloadingMetadata metadata = getClassMap().get(aclass.getName());

        if (metadata == null)
            return aclass;

        if (!assertScriptingEngine(metadata)) {
            return null;
        }
        if (!metadata.isTainted()) {
            //if not tained then we can recycle the last class loaded
            return metadata.getAClass();
        }
        synchronized (BaseWeaver.class) {
            //another chance just in case someone has reloaded between
            //the last if and synchronized, that way we can reduce the number of waiting threads
            if (!metadata.isTainted()) {
                //if not tained then we can recycle the last class loaded
                return metadata.getAClass();
            }
            return loadScriptingClassFromFile(metadata.getSourcePath(), metadata.getFileName());
        }
    }

    /**
     * recompiles and loads a scripting class from a given classname
     *
     * @param className the classname including the package
     * @return a valid class if the sources could be found null if nothing could be found
     */
    public Class loadScriptingClassFromName(String className) {
        if (className.contains("TestBean2")) {
            getLog().debug("debugpoint found");
        }

        Map<String, ReloadingMetadata> classMap = getClassMap();
        ReloadingMetadata metadata = classMap.get(className);
        if (metadata == null) {
            String fileName = className.replaceAll("\\.", File.separator) + getFileEnding();

            for (String pathEntry : getScriptPaths()) {

                /**
                 * the reload has to be performed synchronized
                 * hence there is no chance to do it unsynchronized
                 */
                synchronized (BaseWeaver.class) {
                    metadata = classMap.get(className);
                    if (metadata != null) {
                        return reloadScriptingClass(metadata.getAClass());
                    }
                    Class retVal = (Class) loadScriptingClassFromFile(pathEntry, fileName);
                    if (retVal != null) {
                        return retVal;
                    }
                }
            }

        } else {
            return reloadScriptingClass(metadata.getAClass());
        }
        return null;
    }

    //TODO move this into the classloader to cover dependend classes as well
    protected void refreshReloadingMetaData(String sourceRoot, String file, File currentClassFile, Class retVal, int engineType) {
        ReloadingMetadata reloadingMetaData = new ReloadingMetadata();
        reloadingMetaData.setAClass(retVal);

        reloadingMetaData.setFileName(file);
        reloadingMetaData.setSourcePath(sourceRoot);
        reloadingMetaData.setTimestamp(currentClassFile.lastModified());
        reloadingMetaData.setTainted(false);
        reloadingMetaData.setScriptingEngine(engineType);
        //ReloadingMetadata oldMetadata = getClassMap().get(retVal.getName());
        reloadingMetaData.setTaintedOnce(getClassMap().containsKey(retVal.getName()));

        getClassMap().put(retVal.getName(), reloadingMetaData);
    }

    protected Log getLog() {
        return LogFactory.getLog(this.getClass());
    }

    protected boolean assertScriptingEngine(ReloadingMetadata reloadMeta) {
        return reloadMeta.getScriptingEngine() == getScriptingEngine();
    }


    public String getFileEnding() {
        return fileEnding;
    }

    public void setFileEnding(String fileEnding) {
        this.fileEnding = fileEnding;
    }

    public int getScriptingEngine() {
        return scriptingEngine;
    }

    public void setScriptingEngine(int scriptingEngine) {
        this.scriptingEngine = scriptingEngine;
    }


    protected abstract Class loadScriptingClassFromFile(String sourceRoot, String file);

    public abstract boolean isDynamic(Class clazz);

    public List<String> getScriptPaths() {
        return scriptPaths;
    }

    public ScriptingWeaver getWeaverInstance(Class weaverClass) {
        if (getClass().equals(weaverClass)) return this;

        return null;
    }

    public void fullAnnotationScan() {
    }


    public void requestRefresh() {
        if (  //startup or full recompile conditionr reached
                FileChangedDaemon.getInstance().getSystemRecompileMap().get(getScriptingEngine()) == null ||
                FileChangedDaemon.getInstance().getSystemRecompileMap().get(getScriptingEngine())
                ) {
            fullRecompile();
            //TODO if managed beans are tainted we have to do a full drop

            refreshManagedBeans();
        }
    }

    protected void refreshManagedBeans() {
        if (FacesContext.getCurrentInstance() == null) {
            return;//no npe allowed
        }
        Set<String> tainted = new HashSet<String>();
        for (Map.Entry<String, ReloadingMetadata> it : FileChangedDaemon.getInstance().getClassMap().entrySet()) {
            if (it.getValue().getScriptingEngine() == getScriptingEngine() && it.getValue().isTainted()) {
                tainted.add(it.getKey());
            }
        }
        if (tainted.size() > 0) {
            boolean managedBeanTainted = false;
            //We now have to check if the tainted classes belong to the managed beans
            Set<String> managedBeanClasses = new HashSet<String>();
            Map<String, ManagedBean> mbeans = RuntimeConfig.getCurrentInstance(FacesContext.getCurrentInstance().getExternalContext()).getManagedBeans();
            for (Map.Entry<String, ManagedBean> entry : mbeans.entrySet()) {
                managedBeanClasses.add(entry.getValue().getManagedBeanClassName());
            }
            for (String taintedClass : tainted) {
                if (managedBeanClasses.contains(taintedClass)) {
                    managedBeanTainted = true;
                    break;
                }
            }

            getLog().info("[EXT-SCRIPTING] Tainting all beans to avoid classcast exceptions");
            if (managedBeanTainted) {
                for (Map.Entry<String, ManagedBean> entry : mbeans.entrySet()) {
                    Class managedBeanClass = entry.getValue().getManagedBeanClass();
                    if (WeavingContext.isDynamic(managedBeanClass)) {
                        //managed bean class found we drop the class from our session
                        removeBeanReferences(entry.getValue());
                    }
                    //one bean tainted we have to taint all dynamic beans otherwise we will get classcast
                    //exceptions
                    getLog().info("[EXT-SCRIPTING] Tainting ");
                    ReloadingMetadata metaData = FileChangedDaemon.getInstance().getClassMap().get(managedBeanClass.getName());
                    if(metaData != null) {
                        metaData.setTainted(true);
                    }    
                }

            }
        }
    }

    /**
     * removes the references from out static scope
     * for jsf2 we probably have some kind of notification mechanism
     * which notifies custom scopes
     *
     * @param bean
     */
    private void removeBeanReferences(ManagedBean bean) {
        getLog().info("[EXT-SCRIPTING] JavaScriptingWeaver.removeBeanReferences(" + bean.getManagedBeanName() + ")");

        String scope = bean.getManagedBeanScope();

        if (scope != null && scope.equalsIgnoreCase("session")) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(bean.getManagedBeanName());
        } else if (scope != null && scope.equalsIgnoreCase("application")) {
            FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().remove(bean.getManagedBeanName());
        } else if (scope != null) {
            Object scopeImpl = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(scope);
            if (scopeImpl == null) return; //scope not implemented
            //we now have to revert to introspection here because scopes are a pure jsf2 construct
            //so we use a messaging pattern here to cope with it
            ReflectUtil.executeMethod(scopeImpl, "remove", bean.getManagedBeanName());
        }
    }

}
