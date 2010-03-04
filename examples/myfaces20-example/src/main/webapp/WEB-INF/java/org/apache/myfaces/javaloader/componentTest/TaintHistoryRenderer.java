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

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.scripting.components.CompilerComponent;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;
import org.apache.myfaces.scripting.sandbox.compiler.CompilationResult;
import org.apache.myfaces.shared_impl.renderkit.html.HtmlTextRendererBase;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.*;
import javax.faces.render.FacesRenderer;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

/**
 * A renderer which displays our taint history
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@FacesRenderer(componentFamily = "javax.faces.Output", rendererType = "org.apache.myfaces.scripting.components.TaintHistoryRenderer")
public class TaintHistoryRenderer extends HtmlTextRendererBase  {
    static Logger _log = Logger.getLogger(TaintHistoryRenderer.class.getName());


    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        ResponseWriter wrtr = FacesContext.getCurrentInstance().getResponseWriter();

        startDiv(component, wrtr, "historyBox");
        int lastTainted = ((TaintHistory) component).getNoEntries();

        Collection<ReloadingMetadata> result = WeavingContext.getRefreshContext().getLastTainted(lastTainted);
        if (result == null || result.isEmpty()) {
            wrtr.write("No taint history found");
        } else {
            writeHistory(component, wrtr, result);
        }
        endDiv(wrtr);

        wrtr.flush();

    }

    private void writeHistory(UIComponent component, ResponseWriter wrtr, Collection<ReloadingMetadata> result) throws IOException {
        startDiv(component, wrtr, "history");
        for (ReloadingMetadata entry : result) {
            startDiv(component, wrtr, "line");
            writeDiv(component, wrtr, "timestamp", DateFormat.getInstance().format(new Date(entry.getTimestamp())));
            writeDiv(component, wrtr, "changedFile", entry.getFileName());
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
        wrtr.endElement("div");
    }

    private void startDiv(UIComponent component, ResponseWriter wrtr, String styleClass) throws IOException {
        wrtr.startElement("div", component);
        wrtr.writeAttribute("class", styleClass, null);
    }

    private void writeErrorsLabel(UIComponent component, ResponseWriter wrtr, CompilerComponent compilerComp) throws IOException {
        if (!StringUtils.isBlank(compilerComp.getErrorsLabel())) {
            startDiv(component, wrtr, "errorsLabel");
            wrtr.write(compilerComp.getErrorsLabel());
            endDiv(wrtr);
        }
    }

    private void copyCompilationResult(CompilationResult result, CompilationResult tempResult) {
        result.getErrors().addAll(tempResult.getErrors());
        result.getWarnings().addAll(tempResult.getWarnings());
    }
}
