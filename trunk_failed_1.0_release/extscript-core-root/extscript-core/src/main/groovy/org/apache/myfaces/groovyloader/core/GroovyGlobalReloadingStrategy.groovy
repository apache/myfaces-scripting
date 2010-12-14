package org.apache.myfaces.groovyloader.core

import org.apache.myfaces.extensions.scripting.api.ReloadingStrategy
import org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy
import org.apache.myfaces.extensions.scripting.api.BaseWeaver
import org.apache.myfaces.extensions.scripting.core.reloading.NoMappingReloadingStrategy
import org.apache.myfaces.extensions.scripting.api.ScriptingWeaver;

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
public class GroovyGlobalReloadingStrategy extends org.apache.myfaces.extensions.scripting.core.reloading.GlobalReloadingStrategy {

  //we cannot use a constructor here to bypass a groovy bug
  //we use an explicit call to setWeaver instead

  public GroovyGlobalReloadingStrategy() {
    super();
    _allOthers = new StandardGroovyReloadingStrategy()
  }

  public void setWeaver(ScriptingWeaver weaver) {
    super.setWeaver(weaver)
    _allOthers.setWeaver(weaver)
  }

}