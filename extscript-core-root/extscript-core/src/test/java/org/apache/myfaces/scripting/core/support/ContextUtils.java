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

package org.apache.myfaces.scripting.core.support;

import org.apache.commons.io.FileUtils;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.core.util.WeavingContextInitializer;
import org.apache.myfaces.scripting.loaders.java.compiler.CompilerFacade;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Context utils which store the reusable test code
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ContextUtils {
    /**
     * A startup routine shared by many tests
     * to do the basic weaving initialization
     *
     * @return the mockup servlet context
     */
    public static MockServletContext startupSystem() {
        MockServletContext context = new MockServletContext();
        WeavingContextInitializer.initWeavingContext(context);
        return context;
    }

    /**
     * same as the other one but with a web.xml path being possible
     * @param webXmlPath the path to the web.xml
     * @return the servlet context
     */
    public static MockServletContext startupSystem(String webXmlPath) {
        MockServletContext context = new MockServletContext(webXmlPath);
        WeavingContextInitializer.initWeavingContext(context);
        return context;
    }


    public static File doJavaRecompile(String sourceRoot) throws ClassNotFoundException {
        DynamicCompiler compiler = new CompilerFacade(false);
        try {
            FileUtils.deleteDirectory(WeavingContext.getConfiguration().getCompileTarget());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        WeavingContext.getConfiguration().getCompileTarget().mkdirs();
        return compiler.compileAllFiles(sourceRoot, "");

    }

}
