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
package org.apache.myfaces.javaloader.componentTest;

import org.apache.myfaces.shared_impl.renderkit.html.HtmlTextRendererBase;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Renderer Example with dynamic annotations,
 *          you can move the annotation from one
 *          renderer artifact to the other
 */

/*
 * the annotation is dynamic you can change it on the fly or move it from one
 * class to the other
 */
@FacesRenderer(componentFamily = "javax.faces.Input", rendererType = "at.irian.JavaTestRenderer")
    public class JavaTestRenderer1 extends HtmlTextRendererBase {

    static Log log = LogFactory.getLog(JavaTestRenderer1.class);
    
    private static final String MSG2 = "Hello world from Renderer 1";

    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);
        JavaTestComponent myComponent = (JavaTestComponent) component;
        
        ResponseWriter writer = context.getResponseWriter();
        test(myComponent, writer);
        
        //hello(writer);
        writer.flush();
    }

    private void test(JavaTestComponent myComponent, ResponseWriter writer) throws IOException {
        writer.write(MSG2);
        writer.write(myComponent.getMarker());
        writer.write("<h1/>TestAttr: "+myComponent.getTestAttr()+" | "+myComponent.getTestAttr3()+"</h1>");
        /*lets add the new attribute as extra output*/
        writer.write("<h2>"+myComponent.getTestAttr4()+"</h2>");
    }

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        log.info("JavaTestRenderer1.encodeEnd");
    }

}