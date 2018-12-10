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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.myfaces.extensions.scripting.core.api.Configuration;
import org.apache.myfaces.extensions.scripting.core.api.ReloadingStrategy;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;
import org.apache.myfaces.extensions.scripting.core.engine.api.ScriptingEngine;
import org.apache.myfaces.extensions.scripting.core.engine.compiler.GroovyCompiler;
import org.apache.myfaces.extensions.scripting.core.engine.compiler.ScalaCompiler;
import org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy;
import scala.ScalaObject;

import javax.servlet.ServletContext;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class EngineScala extends BaseEngine implements ScriptingEngine
{
    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void init(ServletContext context)
    {
        initPaths(context, INIT_PARAM_CUSTOM_SCALA_LOADER_PATHS, SCALA_SOURCE_ROOT);
    }

    @Override
    public int getEngineType()
    {
        return ENGINE_TYPE_JSF_SCALA;
    }

    public String getEngineTypeAsStr()
    {
        return "Scala";
    }

    @Override
    public ReloadingStrategy getBasicReloadingStrategy()
    {
        return new SimpleReloadingStrategy();
    }

    @Override
    public boolean isArtifactOfEngine(Object artifact)
    {
        return (artifact instanceof ScalaObject);
    }

    @Override
    public void copyProperties(Object dest, Object src)
    {
        try
        {
            BeanUtils.copyProperties(dest, src);
        }
        catch (IllegalAccessException e)
        {
            log.log(Level.FINEST, e.toString());
            //this is wanted
        }
        catch (InvocationTargetException e)
        {
            log.log(Level.FINEST, e.toString());
            //this is wanted
        }
    }


    @Override
    public String getFileEnding()
    {
        return "scala";
    }

    @Override
    //full compile
    public CompilationResult compile()
    {
        WeavingContext context = WeavingContext.getInstance();
        Configuration configuration = context.getConfiguration();
        ScalaCompiler compiler = new ScalaCompiler();
        File targetDir = configuration.getCompileTarget();
        Collection<String> sourceDirs = configuration.getSourceDirs(ENGINE_TYPE_JSF_SCALA);
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
        ScalaDependencyScanner scanner = new ScalaDependencyScanner();
        scanner.scanPaths();
        log.info("[EXT-SCRIPTING] ending dependency scan");
    }
}
