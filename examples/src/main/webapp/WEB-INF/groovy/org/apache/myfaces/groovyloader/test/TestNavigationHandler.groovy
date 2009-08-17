package org.apache.myfaces.groovyloader.test

import javax.faces.application.NavigationHandler
import javax.faces.context.FacesContext

/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 13.05.2008
 * Time: 19:09:39
 * To change this template use File | Settings | File Templates.
 */
class TestNavigationHandler extends NavigationHandler {
    NavigationHandler _delegate = null;

    public TestNavigationHandler() {
        super();

    }

    public TestNavigationHandler(NavigationHandler delegate) {
        super();
        _delegate = delegate
    }



    public void handleNavigation(FacesContext facesContext, String s, String s1) {
        // if(delegate != null)
        _delegate.handleNavigation(facesContext, s, s1)
        print " handle navigation"
    }

}