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

import org.apache.myfaces.renderkit.html.HtmlFormRenderer;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.support.MockServletContext;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.core.util.WeavingContextInitializer;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;
import org.apache.myfaces.test.base.AbstractJsfTestCase;
import org.apache.myfaces.test.mock.MockRenderKitFactory;
import org.apache.myfaces.test.mock.MockResponseWriter;

import javax.faces.component.html.HtmlForm;
import javax.servlet.ServletContext;
import java.io.StringWriter;

/**
 * Test cases for the taint history component and renderer
 * (note the filter attribute currently is not yet
 * tested for semantic usage because we dont have it implemented yet)
 * 
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class TaintHistoryTest extends AbstractJsfTestCase {

    ServletContext context;
    private TaintHistory _taintHistory;
    private TaintHistoryRenderer _taintHistoryRenderer;
    private HtmlForm _form;
    private MockResponseWriter _writer;
    private static final String VAL_FILTER = "bla";

    public TaintHistoryTest() {
        super(TaintHistoryTest.class.getName());
    }

    public void setUp() throws Exception {
        super.setUp();
        context = new MockServletContext();
        WeavingContextInitializer.initWeavingContext(context);

        _writer = new MockResponseWriter(new StringWriter(), null, null);

        facesContext.setResponseWriter(_writer);
        _taintHistory = new TaintHistory();
        _form = new HtmlForm();
        _taintHistory.setParent(_form);

        facesContext.getViewRoot().setRenderKitId(MockRenderKitFactory.HTML_BASIC_RENDER_KIT);
        facesContext.getRenderKit().addRenderer(
                _taintHistory.getFamily(),
                _taintHistory.getRendererType(),
                new TaintHistoryRenderer());
        facesContext.getRenderKit().addRenderer(
                _form.getFamily(),
                _form.getRendererType(),
                new HtmlFormRenderer());

    }

    public void testNoTaintHistory() throws Exception {
        _taintHistory.encodeAll(facesContext);
        facesContext.renderResponse();
        assertTrue("no taint history found", _writer.getWriter().toString().contains(RendererConst.NO_TAINT_HISTORY_FOUND));
    }

    public void testTaintHistory() throws Exception {
        ReloadingMetadata historyEntry = new ReloadingMetadata();
        historyEntry.setAClass(this.getClass());
        historyEntry.setTimestamp(System.currentTimeMillis());
        historyEntry.setScriptingEngine(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
        historyEntry.setFileName("booga.java");
        historyEntry.setTainted(true);
        historyEntry.setTaintedOnce(true);
        WeavingContext.getRefreshContext().addTaintLogEntry(historyEntry);

        _taintHistory.encodeAll(facesContext);
        facesContext.renderResponse();
        assertFalse("taint history found", _writer.getWriter().toString().contains(RendererConst.NO_TAINT_HISTORY_FOUND));

        assertTrue(_writer.getWriter().toString().contains("booga.java"));
    }

    public void testSaveRestore() {
        _taintHistory.setFilter(VAL_FILTER);
        _taintHistory.setNoEntries(10);
        Object state = _taintHistory.saveState(facesContext);
        _taintHistory.setFilter("");
        _taintHistory.setNoEntries(0);
        _taintHistory.restoreState(facesContext, state);

        assertTrue(_taintHistory.getFilter().equals(VAL_FILTER));

        assertTrue(_taintHistory.getNoEntries().equals(10));

    }

    public void testNoEntries() throws Exception {
        int noEntries = 10;
        for (int cnt = 0; cnt < 100; cnt++) {
            ReloadingMetadata historyEntry = new ReloadingMetadata();
            historyEntry.setAClass(this.getClass());
            historyEntry.setTimestamp(System.currentTimeMillis());
            historyEntry.setScriptingEngine(ScriptingConst.ENGINE_TYPE_JSF_JAVA);
            if(cnt < 10)
                historyEntry.setFileName("0"+cnt + "_booga.java");
            else
                historyEntry.setFileName(cnt + "_booga.java");
            historyEntry.setTainted(true);
            historyEntry.setTaintedOnce(true);
            WeavingContext.getRefreshContext().addTaintLogEntry(historyEntry);
        }

        _taintHistory.setNoEntries(10);
        _taintHistory.encodeAll(facesContext);
        facesContext.renderResponse();

        assertTrue(_writer.getWriter().toString().contains("99_booga.java"));
        assertFalse(_writer.getWriter().toString().contains("89_booga.java"));
        assertFalse(_writer.getWriter().toString().contains("00_booga.java"));
    }

    public void testElAttributes() {
       assertTrue(_taintHistory.getFilter() == null);
       assertTrue(_taintHistory.getNoEntries().equals(TaintHistory.DEFAULT_NO_ENTRIES));
       _taintHistory.setNoEntries(null); 

       _taintHistory.getAttributes().put("noEntries", 20);
       _taintHistory.getAttributes().put("filter", VAL_FILTER);

       assertTrue(_taintHistory.getNoEntries() == 20);
       assertTrue(_taintHistory.getFilter().equals(VAL_FILTER));
    }

}

