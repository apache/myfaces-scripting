package org.apache.myfaces.scripting.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Generic strategy for reloading
 *          this should encapsule various
 *          reloading strategies
 *          which have to be applied depending
 *          on the artefact
 */
public interface ReloadingStrategy {
    public Object reload(Object toReload, int artefactType);

    public ScriptingWeaver getWeaver();

    public void setWeaver(ScriptingWeaver weaver);

}
