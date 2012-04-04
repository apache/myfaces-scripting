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

package org.apache.myfaces.extensions.scripting.core.engine.compiler;

import org.junit.Test;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationException;

import static org.junit.Assert.assertTrue;

/**
 * dummy test for the class to have class level coverage
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class CompilationExceptionTest {
    private static final String ERR_MSG = "BlaMessage";

    @Test
    public void testCompilationException() {
        CompilationException ex = new CompilationException(ERR_MSG);

        assertTrue(ex.getMessage().equals(ERR_MSG));

        ex = new CompilationException(ERR_MSG, new NullPointerException(ERR_MSG));
        assertTrue(ex.getMessage().equals(ERR_MSG));
        assertTrue(ex.getCause() instanceof NullPointerException);

        ex = new CompilationException(new NullPointerException(ERR_MSG));
        assertTrue(ex.getCause() instanceof NullPointerException);
    }

}
