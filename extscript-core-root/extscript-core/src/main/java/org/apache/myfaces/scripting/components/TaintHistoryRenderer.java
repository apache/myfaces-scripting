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

package org.apache.myfaces.scripting.components;

import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * A renderer which displays our taint history
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@SuppressWarnings("unchecked")
public class TaintHistoryRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        ResponseWriter responseWriter = FacesContext.getCurrentInstance().getResponseWriter();

        startDiv(component, responseWriter, "historyBox");
        int lastTainted = ((TaintHistory) component).getNoEntries();

        Collection<ReloadingMetadata> result = WeavingContext.getRefreshContext().getLastTainted(lastTainted);
        if (result == null || result.isEmpty()) {
            responseWriter.write(RendererConst.NO_TAINT_HISTORY_FOUND);
        } else {
            writeHistory(component, responseWriter, result);
        }
        endDiv(responseWriter);

        responseWriter.flush();

    }

    private void writeHistory(UIComponent component, ResponseWriter wrtr, Collection<ReloadingMetadata> result) throws IOException {
        startDiv(component, wrtr, "history");
        for (ReloadingMetadata entry : result) {
            startDiv(component, wrtr, RendererConst.LINE);
            writeDiv(component, wrtr, RendererConst.TIMESTAMP, DateFormat.getInstance().format(new Date(entry.getTimestamp())));
            writeDiv(component, wrtr, RendererConst.CHANGED_FILE, entry.getFileName());
            endDiv(wrtr);
        }

        endDiv(wrtr);
    }

    private String writeDiv(UIComponent component, ResponseWriter wrtr, String styleClass, String value) throws IOException {
        startDiv(component, wrtr, styleClass);
        wrtr.write(value);
        endDiv(wrtr);
        return "";
    }

    private void endDiv(ResponseWriter wrtr) throws IOException {
        wrtr.endElement(RendererConst.HTML_DIV);
    }

    private void startDiv(UIComponent component, ResponseWriter wrtr, String styleClass) throws IOException {
        wrtr.startElement(RendererConst.HTML_DIV, component);
        wrtr.writeAttribute(RendererConst.HTML_CLASS, styleClass, null);
    }

}
