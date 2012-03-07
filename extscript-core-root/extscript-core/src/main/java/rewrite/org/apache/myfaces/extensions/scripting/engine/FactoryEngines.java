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
package rewrite.org.apache.myfaces.extensions.scripting.engine;

import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.FileStrategy;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;
import rewrite.org.apache.myfaces.extensions.scripting.engine.api.ScriptingEngine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          holds references to the scripting engines
 *          initializes the engins dynamically
 */
public class FactoryEngines
{
    final Logger _log = Logger.getLogger(this.getClass().getName());

    Map<Integer, ScriptingEngine> _engines = new ConcurrentHashMap<Integer, ScriptingEngine>();
    List<ScriptingEngine> _engineOrder = new CopyOnWriteArrayList<ScriptingEngine>();

    public void init() throws IOException
    {
        //loadEnginesDynamically();

        EngineJava javaEngine = new EngineJava();
        EngineGroovy groovyEngine = new EngineGroovy();
        if (_engines.isEmpty())
        {
            _engines.put(javaEngine.getEngineType(), javaEngine);
            _engines.put(groovyEngine.getEngineType(), groovyEngine);
            _engineOrder.add(javaEngine);
            _engineOrder.add(groovyEngine);
        }
    }

    /**
     * loads the engins dynamically from
     * their corresponding package and name
     *
     * @throws IOException
     */
    private void loadEnginesDynamically() throws IOException
    {
        ClassLoader currentLoader = ClassUtils.getContextClassLoader();//this.getClass().getClassLoader();
        String canonicalPackageName = this.getClass().getPackage().getName().replaceAll("\\.", File.separator);
        //TODO not working in a servlet environment we for now map it hardcoded
        Enumeration<URL> enumeration = currentLoader.getResources(canonicalPackageName);
        while (enumeration.hasMoreElements())
        {
            //we load all classes which start with engine initially those are our
            //enginesvTH
            URL element = enumeration.nextElement();
            File file = new File(element.getFile());
            FileStrategy strategy = new FileStrategy(Pattern.compile("engine[^\\.(test)]+\\.class$"));
            FileUtils.listFiles(file, strategy);
            for (File foundFile : strategy.getFoundFiles())
            {
                String absoluteDir = foundFile.getAbsolutePath();

                //TODO windows
                String rootDir = absoluteDir.substring(0, absoluteDir.indexOf(canonicalPackageName));
                String className = absoluteDir.substring(rootDir.length()).replaceAll(File.separator, ".");
                className = className.substring(0, className.length() - 6);
                try
                {
                    ScriptingEngine engine = (ScriptingEngine) ReflectUtil.instantiate(currentLoader.loadClass
                            (className));
                    _engines.put(engine.getEngineType(), engine);
                    String supportedLanguage = className.substring(className.indexOf(".Engine") + ".Engine".length
                            ());
                    _log.info("[EXT-SCRIPTING] initializing Engine " + supportedLanguage);
                    _engineOrder.add(engine);
                }
                catch (ClassNotFoundException e)
                {
                    //cannot happen
                }
            }
        }
    }

    public Collection<ScriptingEngine> getEngines()
    {
        return _engineOrder;
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
