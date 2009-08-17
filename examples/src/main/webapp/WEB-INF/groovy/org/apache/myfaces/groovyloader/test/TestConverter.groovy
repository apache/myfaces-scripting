package org.apache.myfaces.groovyloader.test

import javax.faces.convert.Converter
import javax.faces.context.FacesContext
import javax.faces.component.UIComponent

/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 13.05.2008
 * Time: 21:35:15
 * To change this template use File | Settings | File Templates.
 */
class TestConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        print "converting $s"
        return s;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        print "converting back  aa"
        return ((String) o);
    }

}