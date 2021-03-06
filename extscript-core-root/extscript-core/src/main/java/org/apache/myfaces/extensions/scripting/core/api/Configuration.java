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

package org.apache.myfaces.extensions.scripting.core.api;

import org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import org.apache.myfaces.extensions.scripting.core.engine.FactoryEngines;
import org.apache.myfaces.extensions.scripting.core.engine.api.ScriptingEngine;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.myfaces.extensions.scripting.core.api.ScriptingConst.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class Configuration
{
    private static final String WEB_INF_CLASSES = "/WEB-INF/classes";
    List<String> _additionalClassPath = new CopyOnWriteArrayList<String>();
    /**  kk
     * the package whitelist used by our system
     * to determine which packages are under control.
     * <p>&nbsp;</p>
     * Note an empty whitelist means, all packages with sourcedirs attached to.
     */
    Set<String> _packageWhiteList = new ConcurrentSkipListSet<String>();
    /**
     * we keep track of separate resource dirs
     * for systems which can use resource loaders
     * <p>&nbsp;</p>
     * so that we can load various resources as well
     * from separate source directories instead
     */
    volatile List<String> _resourceDirs = new CopyOnWriteArrayList<String>();

    String _initialCompile;

    WeakReference<ServletContext> _contextWeakReference = null;

    /**
     * the target compile path
     */
    volatile File _compileTarget = FileUtils.getTempDir();

    /**
     * the source dirs per scripting engine
     */
    volatile Map<Integer, CopyOnWriteArrayList<String>> _sourceDirs = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>();

    public Configuration(ServletContext context)
    {
        init(context);
    }

    public Configuration()
    {
    }


    private Collection<String> performWildCardSearch(String classPathEntry) {

        if(classPathEntry.toLowerCase().endsWith("*.jar")|| classPathEntry.toLowerCase().endsWith("*.zip")){
            //peform a full search of jars on the dir
            classPathEntry = classPathEntry.substring(0, classPathEntry.length()-5);
            File classPathDir = new File(classPathEntry);
            String[] foundFiles = classPathDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip");
                }
            });
            if(foundFiles == null) {
                return Collections.emptyList();
            }
            ArrayList<String> retVal = new ArrayList<String>(foundFiles.length);
            for(String foundFile: foundFiles) {
                retVal.add(classPathEntry+foundFile);
            }
            return retVal;
        }
        return Arrays.asList(new String[] {classPathEntry});
    }

    public void init(ServletContext context)
    {
        String packageWhiteList = context.getInitParameter(INIT_PARAM_SCRIPTING_PACKAGE_WHITELIST);
        packageWhiteList = (packageWhiteList == null) ? "" : packageWhiteList;
        _packageWhiteList.addAll(Arrays.asList(packageWhiteList.split("\\,")));

        String additionalClassPath = context.getInitParameter(INIT_PARAM_SCRIPTING_ADDITIONAL_CLASSPATH);
        additionalClassPath = (additionalClassPath == null) ? "" : additionalClassPath;
        String[] additionalClassPaths = additionalClassPath.split("\\,");
        for(String cp: additionalClassPaths) {
            _additionalClassPath.addAll(performWildCardSearch(cp));
        }

        _additionalClassPath.addAll(Arrays.asList(additionalClassPaths));

        String resourcePath = context.getInitParameter(INIT_PARAM_RESOURCE_PATH);
        resourcePath = (resourcePath == null) ? "" : resourcePath;
        _resourceDirs.addAll(Arrays.asList(resourcePath.split("\\,")));

        _initialCompile = context.getInitParameter(INIT_PARAM_INITIAL_COMPILE);
        //_additionalClassPath = context.getInitParameter(INIT_PARAM_SCRIPTING_ADDITIONAL_CLASSPATH);

        for (ScriptingEngine engine : FactoryEngines.getInstance().getEngines())
        {
            engine.init(context);
        }

        _contextWeakReference = new WeakReference<ServletContext>(context);
    }

    public String getFileEnding(int scriptingEngine)
    {
        switch (scriptingEngine)
        {
            case ENGINE_TYPE_JSF_JAVA:
                return JAVA_FILE_ENDING;
            case ENGINE_TYPE_JSF_GROOVY:
                return GROOVY_FILE_ENDING;
            case ENGINE_TYPE_JSF_JRUBY:
                return JRUBY_FILE_ENDING;
            default:
                throw new UnsupportedOperationException("Engine type unknown");
        }
    }

    public Collection<String> getSourceDirs(int scriptingEngine)
    {
        return WeavingContext.getInstance().getEngine(scriptingEngine).getSourcePaths();
    }

    /**
     * returns a set of whitelisted subdirs hosting the source
     *
     * @param scriptingEngine the scripting engine for which the dirs have to be determined
     *                        (note every scripting engine has a unique integer value)
     * @return the current whitelisted dirs hosting the sources
     */
    public Collection<String> getWhitelistedSourceDirs(int scriptingEngine)
    {
        Collection<String> origSourceDirs = getSourceDirs(scriptingEngine);
        if (_packageWhiteList.isEmpty())
        {
            return origSourceDirs;
        }

        return mergeWhitelisted(origSourceDirs);
    }

    /**
     * merges the whitelisted packages with the sourcedirs and generates a final list
     * which left join of both sets - the ones which do not exist in reality
     *
     * @param origSourceDirs the original source dirs
     * @return the joined existing subset of all directories which exist
     */
    private Collection<String> mergeWhitelisted(Collection<String> origSourceDirs)
    {
        List<String> retVal = new ArrayList<String>(_packageWhiteList.size() * origSourceDirs.size() + origSourceDirs.size());

        for (String whitelisted : _packageWhiteList)
        {
            whitelisted = whitelisted.replaceAll("\\.", FileUtils.getFileSeparatorForRegex());
            for (String sourceDir : origSourceDirs)
            {
                String newSourceDir = sourceDir + File.separator + whitelisted;
                if ((new File(newSourceDir)).exists())
                {
                    retVal.add(newSourceDir);
                }
            }
        }
        return retVal;
    }

    //----------------------- standard setter and getter --------------------------------------

    /**
     * Add a new source dir for the corresponding scripting engine
     *
     * @param scriptingEngine integer value marking the corresponding engine
     * @param sourceDir       the source directory added to the existing source dir list
     */
    public void addSourceDir(int scriptingEngine, String sourceDir)
    {
        if (!WeavingContext.getInstance().getEngine(scriptingEngine).getSourcePaths().contains(sourceDir))
            WeavingContext.getInstance().getEngine(scriptingEngine).getSourcePaths().add(sourceDir);
    }

    public String getSystemClasspath() {
        return System.getProperty("java.class.path");
    }
    
    public List<String> getJarPaths()
    {
        ServletContext context = _contextWeakReference.get();
        Collection<String> relativePaths = _contextWeakReference.get().getResourcePaths("/WEB-INF/lib");
        List<String> ret = new ArrayList<String>(relativePaths.size());
        for (String jarPath : relativePaths)
        {
            try
            {
                ret.add(context.getResource(jarPath).getFile());
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public List<String> getClassesPaths()
    {
        ServletContext context = _contextWeakReference.get();
        Collection<String> relativePaths = _contextWeakReference.get().getResourcePaths(WEB_INF_CLASSES);
        List<String> ret = new ArrayList<String>(relativePaths.size());
        for (String jarPath : relativePaths)
        {
            try
            {
                if(jarPath.startsWith("/WEB-INF/classes")) {
                    //tomcat plugin bug jarpath evaluates to /WEB-INF/classes//WEB-INF/classes
                    jarPath = "/WEB-INF/classes";
                }
                String file = context.getResource(jarPath).getFile();
                int pos = file.indexOf(WEB_INF_CLASSES);
                file = file.substring(0, pos + WEB_INF_CLASSES.length());
                ret.add(file);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public List<String> getAdditionalClassPath()
    {
        return _additionalClassPath;
    }

    public void setAdditionalClassPath(List<String> additionalClassPath)
    {
        _additionalClassPath = additionalClassPath;
    }

    public void addAdditionalClassPath(String additionalClassPath)
    {
        _additionalClassPath.add(additionalClassPath);
    }

    public Set<String> getPackageWhiteList()
    {
        return _packageWhiteList;
    }

    public void setPackageWhiteList(Set<String> packageWhiteList)
    {
        _packageWhiteList = packageWhiteList;
    }

    public void addWhitelistPackage(String pckg)
    {
        _packageWhiteList.add(pckg);
    }

    public List<String> getResourceDirs()
    {
        return _resourceDirs;
    }

    public void setResourceDirs(List<String> resourceDirs)
    {
        _resourceDirs = resourceDirs;
    }

    public void addResourceDir(String resourceDir)
    {
        _resourceDirs.add(resourceDir);
    }

    public String getInitialCompile()
    {
        return _initialCompile;
    }

    public void setInitialCompile(String initialCompile)
    {
        _initialCompile = initialCompile;
    }

    public File getCompileTarget()
    {
        return _compileTarget;
    }

    public File getCompileTarget(String appendix) {
        return new File(_compileTarget.getAbsolutePath()+appendix);
    }

    public void setCompileTarget(File compileTarget)
    {
        _compileTarget = compileTarget;
    }

}

