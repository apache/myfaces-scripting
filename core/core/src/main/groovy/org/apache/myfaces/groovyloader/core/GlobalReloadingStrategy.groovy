package org.apache.myfaces.groovyloader.core

import org.apache.myfaces.scripting.api.ReloadingStrategy
import org.apache.myfaces.scripting.core.reloading.SimpleReloadingStrategy
import org.apache.myfaces.scripting.api.BaseWeaver
import org.apache.myfaces.scripting.core.reloading.NoMappingReloadingStrategy;

/**
 * Reloading strategy for the groovy
 * connectors
 *
 * Groovy has a different behavior, because
 * every attribute normally is reachable even
 * some introspection ones which under no circumstances
 * should be overwritten
 *
 * so er have to set the all others instance var to a specialized reloading strategy
 * and cope with the rest the standard java way by not doing anything
 *
 */
public class GlobalReloadingStrategy extends org.apache.myfaces.scripting.core.reloading.GlobalReloadingStrategy {

    private BaseWeaver _weaver = null;

    public GlobalReloadingStrategy(weaver) {
        super(weaver);
        _allOthers = new StandardGroovyReloadingStrategy();

    }
}