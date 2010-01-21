package org.apache.myfaces.scripting.api;

/**
 * interface for a bean handler
 * which in the long run will allow
 * to hook different frameworks into the core
 * (aka Mojarra, MyFaces)
 */
public interface BeanHandler {

    /**
     * scans all bean dependencies according to
     * their IoC information stored by the runtime
     * (in our case the MyFaces runtime confing)
     * and adds those into our backward referencing dependency map
     * to add further dependency information on IoC level
     * (we can have IoC dependencies which are bound by object
     * types, this is a corner case but it still can happen)
     */
    public void scanDependencies();

    /**
     * refreshes all managed beans,
     * Application, Session,Request and Custom beans
     * <p/>
     * internally a check is performed whether the refresh has to be done or not
     */
    public void refreshAllManagedBeans();

    /**
     * refreshes all personal scoped beans (aka beans which
     * have an assumed lifecycle <= session)
     * <p/>
     * This is needed for multiuser purposes because if one user alters some beans
     * other users have to drop their non application scoped beans as well!
     * <p/>
     * internally a check is performed whether the refresh has to be performed or not
     */
    public void personalScopeRefresh();
}
