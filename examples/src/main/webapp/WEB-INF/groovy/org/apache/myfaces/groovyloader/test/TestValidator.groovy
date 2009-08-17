package org.apache.myfaces.groovyloader.test

import javax.faces.validator.Validator
import javax.faces.context.FacesContext
import javax.faces.component.UIComponent

/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 13.05.2008
 * Time: 21:33:23
 * To change this template use File | Settings | File Templates.
 */
class TestValidator implements Validator {

    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) {
        println "validating aaaa  "
    }

}