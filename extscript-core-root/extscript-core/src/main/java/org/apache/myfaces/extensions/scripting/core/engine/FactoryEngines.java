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
package org.apache.myfaces.extensions.scripting.core.engine;

import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;
import org.apache.myfaces.extensions.scripting.core.engine.api.ScriptingEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p></p>
 *          holds references to the scripting engines
 *          initializes the engins dynamically
 */
public class FactoryEngines
{
    final Logger _log = Logger.getLogger(this.getClass().getName());
    /*we have to keep the order of the engines for the class detection*/
    Map<Integer, ScriptingEngine> _engines = new LinkedHashMap<Integer, ScriptingEngine>();

    public void init() throws IOException
    {
        //loadEnginesDynamically();
        ScriptingEngine javaEngine = new EngineJava();
        ScriptingEngine groovyEngine = null;
        ScriptingEngine scalaEngine = null;
        ScriptingEngine jrubyEngine = null;
        try
        {
            ClassUtils.getContextClassLoader().loadClass("groovy.lang.GroovyObject");
            groovyEngine = (ScriptingEngine) ReflectUtil.instantiate("org.apache.myfaces.extensions.scripting.core" +
                    ".engine.EngineGroovy");
        }
        catch (Exception ex)
        {
        }
        try
        {
            ClassUtils.getContextClassLoader().loadClass("scala.ScalaObject");
            scalaEngine = (ScriptingEngine) ReflectUtil.instantiate("org.apache.myfaces.extensions.scripting.core" +
                    ".engine.EngineScala");
        }
        catch (Exception ex)
        {
        }
        try
        {
            ClassUtils.getContextClassLoader().loadClass("org.jruby.RubyObject");
            jrubyEngine = (ScriptingEngine) ReflectUtil.instantiate("org.apache.myfaces.extensions.scripting.core" +
                    ".engine.EngineJRuby");
        }
        catch (Exception ex)
        {
        }

        if (_engines.isEmpty())
        {
            //We now add the keys as linked hashmap keys
            //so that java always is last hence the class
            //detection has to work from top to bottom
            if (groovyEngine != null)
                _engines.put(groovyEngine.getEngineType(), groovyEngine);
            if (scalaEngine != null)
                _engines.put(scalaEngine.getEngineType(), scalaEngine);
            if (jrubyEngine != null) {
                _engines.put(jrubyEngine.getEngineType(), jrubyEngine);
            }

            _engines.put(javaEngine.getEngineType(), javaEngine);
        }
    }

    public Collection<ScriptingEngine> getEngines()
    {
        List<ScriptingEngine> engineList = new ArrayList<ScriptingEngine>();
        for (Map.Entry<Integer, ScriptingEngine> entry : _engines.entrySet())
        {
            engineList.add(entry.getValue());
        }

        return engineList;
    }

    public ScriptingEngine getEngine(int engineType)
    {
        return _engines.get(engineType);
    }

    private static FactoryEngines _ourInstance = new FactoryEngines();

    public static FactoryEngines getInstance()
    {
        return _ourInstance;
    }

    private FactoryEngines()
    {
    }
}
