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
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationMessage;
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult;
import org.apache.myfaces.extensions.scripting.core.engine.api.ScriptingEngine;
import org.apache.myfaces.extensions.scripting.core.engine.compiler.JSR199Compiler;
import org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy;

import javax.servlet.ServletContext;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.ENGINE_TYPE_JSF_JAVA;
import static org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.INIT_PARAM_CUSTOM_JAVA_LOADER_PATHS;
import static org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.JAVA_SOURCE_ROOT;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class EngineJava extends BaseEngine implements ScriptingEngine
{

    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void init(ServletContext context)
    {
        initPaths(context, INIT_PARAM_CUSTOM_JAVA_LOADER_PATHS, JAVA_SOURCE_ROOT);
    }

    /**
     * full compile will be called cyclicly
     * from the startup and daemon thread
     */
    public CompilationResult compile()
    {
        WeavingContext context = WeavingContext.getInstance();
        Configuration configuration = context.getConfiguration();
        JSR199Compiler compiler = new JSR199Compiler();
        File targetDir = configuration.getCompileTarget();
        Collection<String> sourceDirs = configuration.getSourceDirs(getEngineType());
        CompilationResult res = null;
        for (String sourceRoot : sourceDirs)
        {
            res =  compiler.compile(new File(sourceRoot), targetDir,
                ClassUtils.getContextClassLoader());
            if(res.hasErrors()) {
               for(CompilationMessage msg :res.getErrors()) {
                   log.severe(msg.getMessage());
               }
               // log.severe(res.getCompilerOutput());
            }
        }
        return res;
    }

    public void scanDependencies()
    {
        log.info("[EXT-SCRIPTING] starting dependency scan "+getEngineTypeAsStr());
        JavaDependencyScanner scanner = new JavaDependencyScanner();
        scanner.scanPaths();
        log.info("[EXT-SCRIPTING] ending dependency scan" + getEngineTypeAsStr());
    }

    //-------------------------------------------------------------------------------------

    @Override
    public int getEngineType()
    {
        return ENGINE_TYPE_JSF_JAVA;
    }
    
    public String getEngineTypeAsStr() {
        return "Java";
    }

    @Override
    public ReloadingStrategy getBasicReloadingStrategy()
    {
        return new SimpleReloadingStrategy();
    }

    @Override
    public boolean isArtifactOfEngine(Object artifact)
    {
        return true;
    }

    @Override
    public void copyProperties(Object dest, Object src)
    {

        try {
            BeanUtils.copyProperties(dest, src);
        } catch (IllegalAccessException e) {
            log.log(Level.FINEST, e.toString());
            //this is wanted
        } catch (InvocationTargetException e) {
            log.log(Level.FINEST, e.toString());
            //this is wanted
        }
    }

    @Override
    public String getFileEnding()
    {
        return "java";
    }

}
