package org.apache.myfaces.scripting.core.scanEvents;

import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface SystemEventListener {

    public Set<Integer> supportsEvents();

    public void handleEvent(SystemEvent evt);

}
