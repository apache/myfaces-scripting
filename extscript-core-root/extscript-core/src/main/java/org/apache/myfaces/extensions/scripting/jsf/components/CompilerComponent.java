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
import org.apache.myfaces.extensions.scripting.core.common.util.StringUtils;

import javax.el.ValueExpression;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import java.util.Locale;

/**
 * Compiler component which currently
 * just shows the last compile output in the system
 * <p></p>
 * Not to keep backwards compatibility to JSF 1.2
 * we do not use the StateHelper but go the old route
 * instead
 */
@SuppressWarnings("unused")
public class CompilerComponent extends UIOutput {

    String _scriptingLanguage = null;
    String _errorsLabel = null;
    String _warningsLabel = null;
    private static final String RENDERER_TYPE = "org.apache.myfaces.extensions.scripting.components.CompilerComponentRenderer";
    private static final String ERRORS_LABEL = "errorsLabel";
    private static final String WARNINGS_LABEL = "warningsLabel";
    private static final String SCRIPTING_LANGUAGE = "scriptingLanguage";

    public CompilerComponent() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public Object saveState(FacesContext facesContext) {
        Object values[] = new Object[4];
        values[0] = super.saveState(facesContext);    //To change body of overridden methods use File | Settings | File Templates.
        values[1] = _scriptingLanguage;
        values[2] = _errorsLabel;
        values[3] = _warningsLabel;
        return values;
    }

    @Override
    public void restoreState(FacesContext facesContext, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(facesContext, values[0]);

        _scriptingLanguage = (String) values[1];
        _errorsLabel = (String) values[2];
        _warningsLabel = (String) values[3];
    }


    public String getScriptingLanguage() {
        if (_scriptingLanguage != null) {
            return _scriptingLanguage;
        }
        ValueExpression vb = getValueExpression(SCRIPTING_LANGUAGE);
        return vb != null ? ((String) vb.getValue(getFacesContext().getELContext())) : null;
    }

    public Integer getScriptingLanguageAsInt() {
        if (StringUtils.isBlank(_scriptingLanguage)) {
            return ScriptingConst.ENGINE_TYPE_JSF_ALL;
        } else {
            String scriptingLanguage = _scriptingLanguage.toLowerCase(Locale.getDefault()).trim();
            if (scriptingLanguage.equals("java")) {
                return ScriptingConst.ENGINE_TYPE_JSF_JAVA;
            } else if (_scriptingLanguage.toLowerCase(Locale.getDefault()).trim().equals("groovy")) {
                return ScriptingConst.ENGINE_TYPE_JSF_GROOVY;
            }
        }
        return ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE;
    }

    public void setScriptingLanguage(String scriptingLanguage) {
        _scriptingLanguage = scriptingLanguage;
    }

    public String getErrorsLabel() {
        if (_errorsLabel != null) {
            return _errorsLabel;
        }
        ValueExpression vb = getValueExpression(ERRORS_LABEL);
        return vb != null ? ((String) vb.getValue(getFacesContext().getELContext())) : null;
    }

    public void setErrorsLabel(String _errorsLabel) {
        this._errorsLabel = _errorsLabel;
    }

    public String getWarningsLabel() {
        if (_warningsLabel != null) {
            return _warningsLabel;
        }
        ValueExpression vb = getValueExpression(WARNINGS_LABEL);
        return vb != null ? ((String) vb.getValue(getFacesContext().getELContext())) : null;
    }

    public void setWarningsLabel(String _warningsLabel) {
        this._warningsLabel = _warningsLabel;
    }
}
