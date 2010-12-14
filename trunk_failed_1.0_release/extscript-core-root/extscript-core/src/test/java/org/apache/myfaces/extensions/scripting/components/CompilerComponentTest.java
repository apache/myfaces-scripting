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

package org.apache.myfaces.extensions.scripting.components;

import org.apache.myfaces.extensions.scripting.components.CompilerComponent;
import org.apache.myfaces.extensions.scripting.components.RendererConst;
import org.apache.myfaces.extensions.scripting.components.CompilerComponentRenderer;
import org.apache.myfaces.renderkit.html.HtmlFormRenderer;
import org.apache.myfaces.extensions.scripting.api.CompilationResult;
import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.support.ContextUtils;
import org.apache.myfaces.extensions.scripting.core.support.MockServletContext;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContextInitializer;
import org.apache.myfaces.test.base.AbstractJsfTestCase;
import org.apache.myfaces.test.mock.MockRenderKitFactory;
import org.apache.myfaces.test.mock.MockResponseWriter;

import javax.faces.component.html.HtmlForm;
import javax.servlet.ServletContext;
import java.io.StringWriter;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class CompilerComponentTest extends AbstractJsfTestCase {

    ServletContext context;
    private CompilerComponent _compilerComponent;
    private MockResponseWriter _writer;
    private static final String ERROR_LABEL = "Error:";
    private static final String JAVA = "java";
    private static final String WARNINGS_LABEL = "Warnings:";
    private static final String GROOVY = "groovy";
    private static final String BLANK = "";
    private static final String ERROR_1 = "1_error";
    private static final String ERROR_2 = "2_error";
    private static final String WARNING_1 = "1_warning";
    private static final String WARNING_2 = "2_warning";
    private static final String ATTR_WARNINGS_LABEL = "warningsLabel";
    private static final String ATTR_ERRORS_LABEL = "errorsLabel";
    private static final String ATTR_SCRIPTING_LANGUAGE = "scriptingLanguage";
    private static final String NO_COMPILE_ERRORS_FOUND = "no compile errors found";
    private static final String COMPILE_ERRORS_FOUND = "Compile errors found";
    private static final String BOOGA = "booga";
    private static final String OUTPUT = "output";

    public CompilerComponentTest() {
        super(CompilerComponentTest.class.getName());
    }

    public void setUp() throws Exception {
        super.setUp();
        context = ContextUtils.startupSystem();

        _writer = new MockResponseWriter(new StringWriter(), null, null);

        facesContext.setResponseWriter(_writer);
        _compilerComponent = new CompilerComponent();
        HtmlForm form = new HtmlForm();
        _compilerComponent.setParent(form);

        facesContext.getViewRoot().setRenderKitId(MockRenderKitFactory.HTML_BASIC_RENDER_KIT);
        facesContext.getRenderKit().addRenderer(
                _compilerComponent.getFamily(),
                _compilerComponent.getRendererType(),
                new CompilerComponentRenderer());
        facesContext.getRenderKit().addRenderer(
                form.getFamily(),
                form.getRendererType(),
                new HtmlFormRenderer());

        _compilerComponent.setErrorsLabel(ERROR_LABEL);
        _compilerComponent.setScriptingLanguage(JAVA);
        _compilerComponent.setWarningsLabel(WARNINGS_LABEL);

    }

    public void testIsTransient() throws Exception {
        assertTrue(_compilerComponent.isTransient());
    }

    public void testSaveRestoreState() throws Exception {
        Object state = _compilerComponent.saveState(facesContext);
        _compilerComponent.setErrorsLabel(BLANK);
        _compilerComponent.setScriptingLanguage(BLANK);
        _compilerComponent.setWarningsLabel(BLANK);
        _compilerComponent.restoreState(facesContext, state);

        assertDefaultTestingValues();
    }

    private void assertDefaultTestingValues() {
        assertTrue(_compilerComponent.getErrorsLabel().equals(ERROR_LABEL));
        assertTrue(_compilerComponent.getScriptingLanguage().equals(JAVA));
        assertTrue(_compilerComponent.getWarningsLabel().equals(WARNINGS_LABEL));
        assertTrue(_compilerComponent.getScriptingLanguageAsInt().equals(ScriptingConst.ENGINE_TYPE_JSF_JAVA));
    }

    public void testScriptingLanguageAsInt() {
        assertTrue(_compilerComponent.getScriptingLanguageAsInt().equals(ScriptingConst.ENGINE_TYPE_JSF_JAVA));
        _compilerComponent.setScriptingLanguage(GROOVY);
        assertTrue(_compilerComponent.getScriptingLanguageAsInt().equals(ScriptingConst.ENGINE_TYPE_JSF_GROOVY));
        _compilerComponent.setScriptingLanguage(BLANK);
        assertTrue(_compilerComponent.getScriptingLanguageAsInt().equals(ScriptingConst.ENGINE_TYPE_JSF_ALL));
        _compilerComponent.setScriptingLanguage(BOOGA);
        assertTrue(_compilerComponent.getScriptingLanguageAsInt().equals(ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE));
    }

    public void testElPart() {
        _compilerComponent.setWarningsLabel(null);
        _compilerComponent.setErrorsLabel(null);
        _compilerComponent.setScriptingLanguage(null);

        _compilerComponent.getAttributes().put(ATTR_WARNINGS_LABEL, WARNINGS_LABEL);
        _compilerComponent.getAttributes().put(ATTR_ERRORS_LABEL, ERROR_LABEL);
        _compilerComponent.getAttributes().put(ATTR_SCRIPTING_LANGUAGE, JAVA);

        assertDefaultTestingValues();
    }

    public void testRendererNoErrorsAndWarnings() throws Exception {
        _compilerComponent.encodeAll(facesContext);
        facesContext.renderResponse();
        assertTrue(NO_COMPILE_ERRORS_FOUND, _writer.getWriter().toString().contains(RendererConst.NO_COMPILE_ERRORS));
    }

    public void testRendererNoErrorsAndWarnings2() throws Exception {
        _compilerComponent.setScriptingLanguage(GROOVY);
        _compilerComponent.encodeAll(facesContext);
        facesContext.renderResponse();
        assertTrue(NO_COMPILE_ERRORS_FOUND, _writer.getWriter().toString().contains(RendererConst.NO_COMPILE_ERRORS));
    }

    public void testRendererNoErrorsAndWarnings3() throws Exception {
        _compilerComponent.setScriptingLanguage(BLANK);
        _compilerComponent.encodeAll(facesContext);
        facesContext.renderResponse();
        assertTrue(NO_COMPILE_ERRORS_FOUND, _writer.getWriter().toString().contains(RendererConst.NO_COMPILE_ERRORS));
    }

    public void testRendererNoErrorsAndWarnings4() throws Exception {
        _compilerComponent.setScriptingLanguage(BOOGA);
        _compilerComponent.encodeAll(facesContext);
        facesContext.renderResponse();
        assertTrue(NO_COMPILE_ERRORS_FOUND, _writer.getWriter().toString().contains(RendererConst.NO_COMPILE_ERRORS));
    }

    public void testCompilationResultJava() throws Exception {
        CompilationResult result = getCompilationResult(JAVA);

        WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA, result);

        _compilerComponent.encodeAll(facesContext);
        facesContext.renderResponse();
        assertStandardResponse(JAVA);

    }

    public void testEmptyLabels() throws Exception {
        _compilerComponent.setWarningsLabel("");
        _compilerComponent.setErrorsLabel("");
        CompilationResult result = getCompilationResult(JAVA);

        WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA, result);

        _compilerComponent.encodeAll(facesContext);
        facesContext.renderResponse();
        String response = _writer.getWriter().toString();

        assertFalse(response.contains(WARNINGS_LABEL));
        assertFalse(response.contains(ERROR_LABEL));

    }

    private void assertStandardResponse(String prefix) {
        assertFalse(COMPILE_ERRORS_FOUND, _writer.getWriter().toString().contains(RendererConst.NO_COMPILE_ERRORS));
        assertTrue(COMPILE_ERRORS_FOUND, _writer.getWriter().toString().contains(ERROR_LABEL));

        String response = _writer.getWriter().toString();
        assertTrue(response.contains(WARNINGS_LABEL));
        assertTrue(response.contains(prefix + ERROR_1));
        assertTrue(response.contains(prefix + ERROR_2));
        assertTrue(response.contains(prefix + WARNING_1));
        assertTrue(response.contains(prefix + WARNING_2));
    }

    public void testCompilationResultGroovy() throws Exception {
        CompilationResult result = getCompilationResult(GROOVY);

        WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA, result);

        _compilerComponent.encodeAll(facesContext);
        _compilerComponent.setScriptingLanguage(GROOVY);
        facesContext.renderResponse();
        assertStandardResponse(GROOVY);

    }

    public void testCompilationResultAll() throws Exception {
        CompilationResult result = getCompilationResult(JAVA);

        WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_JAVA, result);
        result = getCompilationResult(GROOVY);
        WeavingContext.setCompilationResult(ScriptingConst.ENGINE_TYPE_JSF_GROOVY, result);

        _compilerComponent.setScriptingLanguage("");
        _compilerComponent.encodeAll(facesContext);
        facesContext.renderResponse();
        assertStandardResponse(JAVA);
        assertStandardResponse(GROOVY);

    }

    private CompilationResult getCompilationResult(String prefix) {
        CompilationResult result = new CompilationResult(OUTPUT);

        result.getErrors().add(new CompilationResult.CompilationMessage(1, prefix + ERROR_1));
        result.getErrors().add(new CompilationResult.CompilationMessage(2, prefix + ERROR_2));

        result.getWarnings().add(new CompilationResult.CompilationMessage(1, prefix + WARNING_1));
        result.getWarnings().add(new CompilationResult.CompilationMessage(2, prefix + WARNING_2));
        return result;
    }

}
