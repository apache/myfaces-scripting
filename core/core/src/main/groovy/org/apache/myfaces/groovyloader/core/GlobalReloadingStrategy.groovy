package org.apache.myfaces.groovyloader.core

import org.apache.myfaces.scripting.api.ReloadingStrategy
import org.apache.myfaces.scripting.core.reloading.SimpleReloadingStrategy
import org.apache.myfaces.scripting.api.BaseWeaver
import org.apache.myfaces.scripting.core.reloading.RendererReloadingStrategy;

/**
 * Reloading strategy for the groovy
 * connectors
 *
 * Groovy has a different behavior, because
 * every attribute normally is reachable even
 * some introspection ones which under no circumstances
 * should be overwritten
 *
 */
public class GlobalReloadingStrategy  implements ReloadingStrategy {

    private BaseWeaver _weaver = null;

    private ReloadingStrategy _rendererStrategy;
    private ReloadingStrategy _allOthers;


    public GlobalReloadingStrategy(weaver) {
        _weaver = weaver;
        _rendererStrategy = new RendererReloadingStrategy();
        _allOthers = new StandardGroovyReloadingStrategy();

    }

    public Object reload(Object toReload, int artefactType) {


        switch (artefactType) {
            case ScriptingConst.ARTEFACT_TYPE_RENDERER:
                return _rendererStrategy.reload(toReload, artefactType);
            //TODO Add other artefact loading strategies on demand here
            default:
                return _allOthers.reload(toReload, artefactType);
        }    }
}