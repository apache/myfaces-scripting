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

import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.StringUtils;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationMessage;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Renderer for the compiler component
 * <p/>
 * This renderer is responsible for rendering the last compiler output
 * hosted in our weavingContext
 */
@SuppressWarnings("unchecked")
public class CompilerComponentRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        ResponseWriter responseWriter = FacesContext.getCurrentInstance().getResponseWriter();
        CompilerComponent compilerComp = (CompilerComponent) component;

        Integer scriptingLanguage = compilerComp.getScriptingLanguageAsInt();
        CompilationResult result = null;
        switch (scriptingLanguage) {
            case ScriptingConst.ENGINE_TYPE_JSF_JAVA:
                result = WeavingContext.getInstance().getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
                break;
            case ScriptingConst.ENGINE_TYPE_JSF_GROOVY:
                result = WeavingContext.getInstance().getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_GROOVY);
                break;
            case ScriptingConst.ENGINE_TYPE_JSF_ALL:
                result = new CompilationResult("");
                CompilationResult tempResult = WeavingContext.getInstance().getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
                if (tempResult != null) {
                    copyCompilationResult(result, tempResult);
                }

                tempResult = WeavingContext.getInstance().getCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_GROOVY);
                if (tempResult != null) {
                    copyCompilationResult(result, tempResult);
                }

                break;
            case ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE:
                Logger log = Logger.getLogger(this.getClass().getName());
                log.warning(RendererConst.WARNING_ENGINE_NOT_FOUND);
                break;
        }

        startDiv(component, responseWriter, RendererConst.ERROR_BOX);
        if (result == null || (!result.hasErrors() && result.getWarnings().isEmpty())) {
            responseWriter.write(RendererConst.NO_COMPILE_ERRORS);
        } else {
            writeErrorsLabel(component, responseWriter, compilerComp);
            writeErrors(component, responseWriter, result);
            writeWarningsLabel(component, responseWriter, compilerComp);
            writeWarnings(component, responseWriter, result);
        }
        endDiv(responseWriter);

        responseWriter.flush();

    }

    private void writeWarnings(UIComponent component, ResponseWriter responseWriter, CompilationResult result) throws IOException {
        startDiv(component, responseWriter, RendererConst.WARNINGS);
        for (CompilationMessage msg : result.getWarnings()) {
            startDiv(component, responseWriter, RendererConst.LINE);
            writeDiv(component, responseWriter, RendererConst.LINE_NO, String.valueOf(msg.getLineNumber()));
            writeDiv(component, responseWriter, RendererConst.MESSAGE, msg.getMessage());
            endDiv(responseWriter);
        }
        endDiv(responseWriter);
    }

    private void writeWarningsLabel(UIComponent component, ResponseWriter responseWriter, CompilerComponent compilerComp) throws IOException {
        if (!StringUtils.isBlank(compilerComp.getWarningsLabel())) {
            startDiv(component, responseWriter, RendererConst.WARNINGS_LABEL);
            responseWriter.write(compilerComp.getWarningsLabel());
            endDiv(responseWriter);
        }
    }

    private void writeErrors(UIComponent component, ResponseWriter responseWriter, CompilationResult result) throws IOException {
        startDiv(component, responseWriter, RendererConst.ERRORS);
        for (CompilationMessage msg : result.getErrors()) {
            startDiv(component, responseWriter, RendererConst.LINE);
            writeDiv(component, responseWriter, RendererConst.LINE_NO, String.valueOf(msg.getLineNumber()));
            writeDiv(component, responseWriter, RendererConst.MESSAGE, msg.getMessage());
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

    private void writeErrorsLabel(UIComponent component, ResponseWriter responseWriter, CompilerComponent compilerComp) throws IOException {
        if (!StringUtils.isBlank(compilerComp.getErrorsLabel())) {
            startDiv(component, responseWriter, RendererConst.ERRORS_LABEL);
            responseWriter.write(compilerComp.getErrorsLabel());
            endDiv(responseWriter);
        }
    }

    private void copyCompilationResult(CompilationResult result, CompilationResult tempResult) {
        result.getErrors().addAll(tempResult.getErrors());
        result.getWarnings().addAll(tempResult.getWarnings());
    }
}
