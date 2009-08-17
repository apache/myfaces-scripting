package org.apache.myfaces.groovyloader.test

import javax.faces.event.ActionListener
import javax.faces.event.ActionEvent

/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 13.05.2008
 * Time: 21:21:20
 * To change this template use File | Settings | File Templates.
 */
class TestActionListener implements ActionListener {

    public void processAction(ActionEvent event) {
        println "processing action from actionlistener"
    }

}

