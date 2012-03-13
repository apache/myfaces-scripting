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

package rewrite.org.apache.myfaces.extensions.scripting.scanningcore.engine.compiler;

import org.apache.myfaces.extensions.scripting.api.CompilationResult;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class CompilationResultTest {
    CompilationResult result = null;
    private static final String BOOGA = "booga";

    @Before
    public void init() {
        result = new CompilationResult(BOOGA);
    }

    @Test
    public void testGetCompilerOutput() throws Exception {
        assertTrue(result.getCompilerOutput().equals(BOOGA));
    }

    @Test
    public void testHasErrors() throws Exception {
        assertFalse(result.hasErrors());
        result.getErrors().add(new CompilationResult.CompilationMessage(1, BOOGA));
        assertTrue(result.hasErrors());
    }

    @Test
    public void testRegisterError() throws Exception {
        assertFalse(result.hasErrors());
        result.registerError(new CompilationResult.CompilationMessage(1, BOOGA));
        assertTrue(result.hasErrors());
    }

    @Test
    public void testGetErrors() throws Exception {
        assertTrue(result.getErrors().isEmpty());
        result.registerError(new CompilationResult.CompilationMessage(1, BOOGA));
        result.registerError(null);
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().size() == 1);
        result.registerError(new CompilationResult.CompilationMessage(1, BOOGA));
        result.registerError(null);
        assertTrue(result.getErrors().size() == 2);

    }

    @Test
    public void testRegisterWarning() throws Exception {
        assertTrue(result.getWarnings().isEmpty());
        result.registerWarning(new CompilationResult.CompilationMessage(1, BOOGA));
        result.registerWarning(null);
        assertFalse(result.getWarnings().isEmpty());
        assertTrue(result.getWarnings().size() == 1);
        result.registerWarning(new CompilationResult.CompilationMessage(1, BOOGA));

        assertFalse(result.getWarnings().isEmpty());
        assertTrue(result.getWarnings().size() == 2);
        assertTrue(result.getErrors().isEmpty());
    }
   
}
