package org.apache.myfaces.scripting.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A set of artifact types which are used internally
 *          so that the reloading strategies and other parts of the system
 *          can adapt to the type of the artifact which has to be reloaded
 */
@SuppressWarnings("unused")
public enum ArtefactType {
    MANAGED_BEAN,
    COMPONENT,
    BEHAVIOR,
    VALIDATOR,
    RENDERER,
    NAV_HANDLER,
    RENDERKIT,
    VIEWHANDLER,
    LIFECYCLE,
    FACESCONTEXT,
    ELRESOLVER,
    VARIABLERESOLVER,
    SCOPE,
    RESOURCEHANDLER
}
