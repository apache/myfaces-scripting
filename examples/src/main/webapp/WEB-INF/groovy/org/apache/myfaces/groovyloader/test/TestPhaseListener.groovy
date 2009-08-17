package org.apache.myfaces.groovyloader.test

import javax.faces.event.PhaseListener
import javax.faces.event.PhaseEvent
import javax.faces.event.PhaseId

/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 06.05.2008
 * Time: 07:18:55
 * To change this template use File | Settings | File Templates.
 */
class TestPhaseListener implements PhaseListener {


    public void afterPhase(PhaseEvent event) {
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE)
            println "restoring a view bbb bbb" + event.getPhaseId()


    }

    public void beforePhase(PhaseEvent event) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

}