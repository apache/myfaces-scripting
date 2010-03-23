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

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.scripting.api.CompilationResult;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.shared_impl.renderkit.html.HtmlTextRendererBase;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Renderer for the compiler component
 * <p/>
 * This renderer is responsible for rendering the last compiler output
 * hosted in our weavingContext
 */
public class CompilerComponentRenderer extends HtmlTextRendererBase {
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        ResponseWriter wrtr = FacesContext.getCurrentInstance().getResponseWriter();
        CompilerComponent compilerComp = (CompilerComponent) component;

        Integer scriptingLanguage = compilerComp.getScriptingLanguageAsInt();
        CompilationResult result = null;
        switch (scriptingLanguage) {
            case ScriptingConst.ENGINE_TYPE_JSF_JAVA:
                result = WeavingContext.getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
                break;
            case ScriptingConst.ENGINE_TYPE_JSF_GROOVY:
                result = WeavingContext.getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
                break;
            case ScriptingConst.ENGINE_TYPE_JSF_ALL:
                result = new CompilationResult("");
                CompilationResult tempResult = WeavingContext.getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
                if (tempResult != null) {
                    copyCompilationResult(result, tempResult);
                }

                tempResult = WeavingContext.getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_GROOVY);
                if (tempResult != null) {
                    copyCompilationResult(result, tempResult);
                }

                break;
            case ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE:
                Logger log = Logger.getLogger(this.getClass().getName());
                log.warning("Warning engine not found");
                break;

        }

        startDiv(component, wrtr, "errorBox");
        if (result == null || (!result.hasErrors() && result.getWarnings().isEmpty())) {
            wrtr.write("No compile errors");
        } else {
            writeErrorsLabel(component, wrtr, compilerComp);
            writeErrors(component, wrtr, result);
            writeWarningsLabel(component, wrtr, compilerComp);
            writeWarnings(component, wrtr, result);
        }
        endDiv(wrtr);

        wrtr.flush();

    }

    private void writeWarnings(UIComponent component, ResponseWriter wrtr, CompilationResult result) throws IOException {
        startDiv(component, wrtr, "warnings");
        for (CompilationResult.CompilationMessage msg : result.getWarnings()) {
            startDiv(component, wrtr, "line");
            writeDiv(component, wrtr, "lineNo", String.valueOf(msg.getLineNumber()));
            writeDiv(component, wrtr, "message", msg.getMessage());
            endDiv(wrtr);
        }
        endDiv(wrtr);
    }

    private void writeWarningsLabel(UIComponent component, ResponseWriter wrtr, CompilerComponent compilerComp) throws IOException {
        if (!StringUtils.isBlank(compilerComp.getWarningsLabel())) {
            startDiv(component, wrtr, "warningsLabel");
            wrtr.write(compilerComp.getWarningsLabel());
            endDiv(wrtr);
        }
    }

    private void writeErrors(UIComponent component, ResponseWriter wrtr, CompilationResult result) throws IOException {
        startDiv(component, wrtr, "errors");
        for (CompilationResult.CompilationMessage msg : result.getErrors()) {
            startDiv(component, wrtr, "line");
            writeDiv(component, wrtr, "lineNo", String.valueOf(msg.getLineNumber()));
            writeDiv(component, wrtr, "message", msg.getMessage());
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
