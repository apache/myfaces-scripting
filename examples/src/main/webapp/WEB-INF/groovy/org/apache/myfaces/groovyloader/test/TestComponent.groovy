package org.apache.myfaces.groovyloader.test

import javax.faces.component.UIInput
import javax.faces.context.FacesContext
import javax.faces.el.ValueBinding

/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 15.05.2008
 * Time: 18:50:10
 * To change this template use File | Settings | File Templates.
 */
public class TestComponent extends UIInput {

    private static final String DEFAULT_RENDERER_TYPE2 = "org.apache.myfaces.groovyloader.test.Test";

    String _testattr = "component text";
    def _testattr_changed = true;
    def testattr_changed = true;


    public TestComponent() {
        super()
        setRendererType(DEFAULT_RENDERER_TYPE2)
    }

    public Object saveState(FacesContext context) {
        def values = []
        values[0] = super.saveState(context)
        values[1] = testattr
        return values.toArray()
    }

    public void restoreState(FacesContext context, Object state) {
        super.restoreState(context, state[0]);
        _testattr = state[1]
    }

    public void setTestattr(String attr) {
        _testattr = attr
    }

    public String getTestattr() {
        if (_testattr != null)
            return _testattr

        ValueBinding vb = getValueBinding("testattr")
        String v = vb != null ? (String)vb.getValue(getFacesContext()) : null
        return v != null ? v : ""
                                
    }


    public String getFamily() {
        return "javax.faces.Input";
    }

}
