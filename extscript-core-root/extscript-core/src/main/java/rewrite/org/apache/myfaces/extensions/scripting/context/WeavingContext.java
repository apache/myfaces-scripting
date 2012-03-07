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

package rewrite.org.apache.myfaces.extensions.scripting.context;

import org.apache.myfaces.extensions.scripting.core.dependencyScan.core.ClassDependencies;
import rewrite.org.apache.myfaces.extensions.scripting.engine.FactoryEngines;
import rewrite.org.apache.myfaces.extensions.scripting.engine.api.ScriptingEngine;
import rewrite.org.apache.myfaces.extensions.scripting.monitor.ClassResource;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Central weaving context
 */

public class WeavingContext
{
    /**
     * lock var which can be used for recompilation
     */
    public AtomicBoolean recompileLock = new AtomicBoolean(false);
    protected Configuration configuration = new Configuration();
    ClassDependencies _dependencyMap = new ClassDependencies();

    Logger log = Logger.getLogger(this.getClass().getName());

    public void initEngines() throws IOException
    {
        FactoryEngines.getInstance().init();
    }

    public Collection<ScriptingEngine> getEngines()
    {
        return FactoryEngines.getInstance().getEngines();
    }

    public ScriptingEngine getEngine(int engineType)
    {
        return FactoryEngines.getInstance().getEngine(engineType);
    }

    /**
     * returns the mitable watche resource maps for the various engines
     *
     * @return
     */
    public Map<Integer, Map<String, ClassResource>> getWatchedResources()
    {
        Map<Integer, Map<String, ClassResource>> ret = new HashMap<Integer, Map<String, ClassResource>>();
        for (ScriptingEngine engine : getEngines())
        {
            ret.put(engine.getEngineType(), engine.getWatchedResources());
        }
        return ret;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }

    public boolean needsRecompile() {
        for (ScriptingEngine engine : getEngines())
        {
            //log.info("[EXT-SCRIPTING] scanning " + engine.getEngineType() + " files");
            if(engine.needsRecompile()) return true;
            //log.info("[EXT-SCRIPTING] scanning " + engine.getEngineType() + " files done");
        }
        return false;
    }
    
    public void initialFullScan()
    {
        for (ScriptingEngine engine : getEngines())
        {
            //log.info("[EXT-SCRIPTING] scanning " + engine.getEngineType() + " files");
            engine.scanForAddedDeleted();
            //log.info("[EXT-SCRIPTING] scanning " + engine.getEngineType() + " files done");
        }
    }

    public boolean compile()
    {
        boolean compile = false;
        for (ScriptingEngine engine : getEngines())
        {
            if (!engine.needsRecompile()) continue;
            compile = true;
            log.info("[EXT-SCRIPTING] compiling " + engine.getEngineTypeAsStr() + " files");
            engine.compile();
            log.info("[EXT-SCRIPTING] compiling " + engine.getEngineTypeAsStr() + " files done");
        }
        return compile;
    }

    public void scanDependencies()
    {
        for (ScriptingEngine engine : getEngines())
        {
            if(engine.isTainted())  {
                log.info("[EXT-SCRIPTING] scanning " + engine.getEngineTypeAsStr() + " dependencies");
                engine.scanDependencies();
                log.info("[EXT-SCRIPTING] scanning " + engine.getEngineTypeAsStr() + " dependencies end");
            }
        }
    }

    public void markTaintedDependends()
    {
        for (ScriptingEngine engine : getEngines())
        {
            engine.markTaintedDependencies();
        }
    }

    //----------------------------------------------------------------------
    public ClassDependencies getDependencyMap()
    {
        return _dependencyMap;
    }

    public void setDependencyMap(ClassDependencies dependencyMap)
    {
        _dependencyMap = dependencyMap;
    }

    protected static WeavingContext _instance = new WeavingContext();

    public static WeavingContext getInstance()
    {
        return _instance;
    }

}
