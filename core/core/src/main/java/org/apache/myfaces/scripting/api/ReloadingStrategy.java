package org.apache.myfaces.scripting.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Generic strategy for reloading
 *          this should encapsulate various
 *          reloading strategies
 *          which have to be applied depending
 *          on the artifact
 */
public interface ReloadingStrategy {
    public Object reload(Object toReload, int artefactType);

    public ScriptingWeaver getWeaver();

    public void setWeaver(ScriptingWeaver weaver);

}
