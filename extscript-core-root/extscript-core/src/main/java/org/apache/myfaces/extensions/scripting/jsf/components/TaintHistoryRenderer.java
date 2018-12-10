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

package org.apache.myfaces.extensions.scripting.jsf.components;


import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.monitor.ClassResource;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;

/**
 * A renderer which displays our taint history
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@SuppressWarnings("unchecked")
//we have to suppress here because of the component cast
public class TaintHistoryRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        ResponseWriter responseWriter = FacesContext.getCurrentInstance().getResponseWriter();

        startDiv(component, responseWriter, "historyBox");
        int lastTainted = ((TaintHistory) component).getNoEntries();

        Collection<ClassResource> result = WeavingContext.getInstance().getLastTainted(lastTainted);
        if (result == null || result.isEmpty()) {
            responseWriter.write(RendererConst.NO_TAINT_HISTORY_FOUND);
        } else {
            writeHistory(component, responseWriter, result);
        }
        endDiv(responseWriter);

        responseWriter.flush();

    }

    private void writeHistory(UIComponent component, ResponseWriter responseWriter,
                              Collection<ClassResource> result) throws IOException {
        startDiv(component, responseWriter, "history");
        for (ClassResource entry : result) {
            startDiv(component, responseWriter, RendererConst.LINE);
            writeDiv(component, responseWriter, RendererConst.TIMESTAMP, DateFormat.getInstance().format(entry.getFile().lastModified()));
            writeDiv(component, responseWriter, RendererConst.CHANGED_FILE, entry.getFile().getAbsolutePath());
            endDiv(responseWriter);
        }

        endDiv(responseWriter);
    }

    private String writeDiv(UIComponent component, ResponseWriter responseWriter, String styleClass, String value) throws IOException {
        startDiv(component, responseWriter, styleClass);
        responseWriter.write(value);
        endDiv(responseWriter);
        return "";
    }

    private void endDiv(ResponseWriter responseWriter) throws IOException {
        responseWriter.endElement(RendererConst.HTML_DIV);
    }

    private void startDiv(UIComponent component, ResponseWriter responseWriter, String styleClass) throws IOException {
        responseWriter.startElement(RendererConst.HTML_DIV, component);
        responseWriter.writeAttribute(RendererConst.HTML_CLASS, styleClass, null);
    }

}
