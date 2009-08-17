package org.apache.myfaces.groovyloader.test

import org.apache.myfaces.shared_tomahawk.renderkit.html.HtmlTextRendererBase
import javax.faces.context.FacesContext
import javax.faces.component.UIComponent;


/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 16.05.2008
 * Time: 15:18:52
 * To change this template use File | Settings | File Templates.
 */
public class TestRenderer extends HtmlTextRendererBase {

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) {
        facesContext.responseWriter.write """
            <h1> Hello from a groovy JSF components renderer </h1>

            <p> you can find my sources under WEB-INF/groovy/... </p>

            <p> you can edit the artefacts is running</p>
            <p> I will pick up the changes after you have hit the reload button </p>

            <h2> One note however, you cannot change components with a simple page
            refresh, this works only for renderers</h2>

            <p> you have to rebuild the component tree</p>

            <p> Once you are done you can move your sources over and have them compiled into
            java classes to gain more speed </p>

            <p> You can prototype every jsf artefact that way, managed beans, renderers, validators,
            phase listeners </p>

            Test for attribute: $uiComponent.testattr
        """
        super.encodeBegin(facesContext, uiComponent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) {
          print super.toString()
           super.encodeEnd(facesContext, uiComponent);    //To change body of overridden methods use File | Settings | File Templates.
        facesContext.getResponseWriter().write """\
            encode end here
        """
        
    }
}