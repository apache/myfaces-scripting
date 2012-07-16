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

package org.apache.myfaces.extensions.scripting.core.support;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.jsf.startup.StartupServletContextPluginChainLoader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic unit testing servlet context mock
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class MockServletContext extends org.apache.myfaces.test.mock.MockServletContext
{

    Map<String, Object> _attributes = new HashMap<String, Object>();
    Map<String, String> _initParameters = new HashMap<String, String>();
    String _resourceRoot = "../../src/test/resources/webapp";

    public MockServletContext()
    {
        setResourceRoot(_resourceRoot);
        addInitParameter(ScriptingConst.INIT_PARAM_MYFACES_PLUGIN, StartupServletContextPluginChainLoader.class.getName());
        //TODO we reroute the init params to
        //our logical groovy and java dirs relative
        //to our classpath
        //ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //URL rootDir = loader.getResource("/");
        //String sRootDir = rootDir.getPath();
        //String resourceDir = sRootDir+File.separator+"webapp";
        String resourceDir = getResourceDir();

        String javaDir = resourceDir    + File.separator + "WEB-INF" + File.separator + "java";
        String groovyDir = resourceDir  + File.separator + "WEB-INF" + File.separator + "groovy";
        String scalaDir = resourceDir   + File.separator + "WEB-INF" + File.separator + "scala";

        addInitParameter(ScriptingConst.INIT_PARAM_CUSTOM_JAVA_LOADER_PATHS, javaDir);
        addInitParameter(ScriptingConst.INIT_PARAM_CUSTOM_SCALA_LOADER_PATHS, scalaDir);
        addInitParameter(ScriptingConst.INIT_PARAM_CUSTOM_GROOVY_LOADER_PATHS, groovyDir);
        addInitParameter(ScriptingConst.INIT_PARAM_RESOURCE_PATH, resourceDir);

    }

    private String getResourceDir()
    {
        //private field access
        Field f = null;
        String resourceDir = null;
        try
        {
            f = org.apache.myfaces.test.mock.MockServletContext.class.getDeclaredField("documentRoot");
            f.setAccessible(true);
            File docRoot = (File) f.get(this);
            resourceDir = docRoot.getAbsolutePath();
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return resourceDir;
    }

    public MockServletContext(String resourceRoot)
    {
        setResourceRoot(resourceRoot);
        addInitParameter(ScriptingConst.INIT_PARAM_MYFACES_PLUGIN, StartupServletContextPluginChainLoader.class.getName());
    }

    public void setResourceRoot(String newRoot)
    {
        _resourceRoot = newRoot;
        try
        {
            super.setDocumentRoot(new File(FilenameUtils.normalize(URLDecoder.decode(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("./")
                    .getPath(), Charset.defaultCharset().toString()) +
                    _resourceRoot)));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

}
