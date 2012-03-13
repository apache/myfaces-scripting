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

package rewrite.org.apache.myfaces.extensions.scripting.scanningcore.support;

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.servlet.StartupServletContextPluginChainLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic unit testing servlet context mock
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class MockServletContext extends org.apache.myfaces.test.mock.MockServletContext {

    Map<String, Object> _attributes = new HashMap<String, Object>();
    Map<String, String> _initParameters = new HashMap<String, String>();
    String _resourceRoot = "../../src/test/resources/webapp";

    public MockServletContext() {
        setResourceRoot(_resourceRoot);
        addInitParameter(ScriptingConst.INIT_PARAM_MYFACES_PLUGIN, StartupServletContextPluginChainLoader.class.getName());
    }

    public MockServletContext(String resourceRoot) {
        setResourceRoot(resourceRoot);
        addInitParameter(ScriptingConst.INIT_PARAM_MYFACES_PLUGIN, StartupServletContextPluginChainLoader.class.getName());
    }

    public void setResourceRoot(String newRoot) {
        _resourceRoot = newRoot;
        super.setDocumentRoot(new File(Thread.currentThread().getContextClassLoader().getResource("./").getPath() + _resourceRoot));
    }

}
