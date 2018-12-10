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

import groovy.lang.GroovyObject;
import org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy;
import org.apache.myfaces.extensions.scripting.groovyloader.core.GroovyPropertyMapper;
import org.apache.myfaces.extensions.scripting.core.api.Configuration;
import org.apache.myfaces.extensions.scripting.core.api.ReloadingStrategy;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;
import org.apache.myfaces.extensions.scripting.core.engine.api.ScriptingEngine;
import org.apache.myfaces.extensions.scripting.core.engine.compiler.GroovyCompiler;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

import static org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class EngineGroovy extends BaseEngine implements ScriptingEngine
{
    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void init(ServletContext context)
    {
        initPaths(context, INIT_PARAM_CUSTOM_GROOVY_LOADER_PATHS, GROOVY_SOURCE_ROOT);
    }

    @Override
    public int getEngineType()
    {
        return ENGINE_TYPE_JSF_GROOVY;
    }

    public String getEngineTypeAsStr() {
        return "Groovy";
    }

    @Override
    public ReloadingStrategy getBasicReloadingStrategy()
    {
        return new SimpleReloadingStrategy();
    }

    @Override
    public boolean isArtifactOfEngine(Object artifact)
    {
        return (artifact instanceof GroovyObject);
    }

    @Override
    public void copyProperties(Object dest, Object src)
    {
        GroovyPropertyMapper.mapProperties(dest, src);
    }

    @Override
    public String getFileEnding()
    {
        return "groovy";
    }

    @Override
    //full compile
    public CompilationResult compile()
    {
        WeavingContext context = WeavingContext.getInstance();
        Configuration configuration = context.getConfiguration();
        GroovyCompiler compiler = new GroovyCompiler();
        File targetDir = configuration.getCompileTarget();
        Collection<String> sourceDirs = configuration.getSourceDirs(ENGINE_TYPE_JSF_GROOVY);
        CompilationResult res = null;
        for (String sourceRoot : sourceDirs)
        {
            res = compiler.compile(new File(sourceRoot), targetDir, ClassUtils.getContextClassLoader());
        }
        return res;
    }

    public void scanDependencies()
    {
        log.info("[EXT-SCRIPTING] starting dependency scan");
        GroovyDependencyScanner scanner = new GroovyDependencyScanner();
        scanner.scanPaths();
        log.info("[EXT-SCRIPTING] ending dependency scan");
    }


}
