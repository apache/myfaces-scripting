/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.groovyloader.test

import org.apache.myfaces.shared_tomahawk.renderkit.html.HtmlTextRendererBase
import javax.faces.context.FacesContext
import javax.faces.component.UIComponent;


/**
 * @author Werner Punz
 */
public class TestRenderer extends HtmlTextRendererBase {

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) {
        facesContext.responseWriter.write """
            <h1>Hello from a groovy JSF components renderer </h1>

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