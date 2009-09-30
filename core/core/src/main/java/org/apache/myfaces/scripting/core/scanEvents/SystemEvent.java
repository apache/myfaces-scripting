package org.apache.myfaces.scripting.core.scanEvents;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface SystemEvent {
    public Integer getEventType();
    public String getAffectedClassName();
    public Integer getArtefactType();
}
