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

package org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.loaders;

import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * TODO replace it with the ThrowAwayLoader
 */
@Deprecated
public class ScannerClassloader extends ClassLoader
{

    File _tempDir = null;

    Map<String, Class> _alreadyScanned = new HashMap<String, Class>();

    final Logger _logger = Logger.getLogger(ScannerClassloader.class.getName());

    public ScannerClassloader(ClassLoader classLoader, int scriptingEngine, String engineExtension, File tempDir)
    {
        super(classLoader);

        this._tempDir = tempDir;
    }

    @Override
    public InputStream getResourceAsStream(String name)
    {
        File resource = new File(_tempDir.getAbsolutePath() + File.separator + name);
        if (resource.exists())
        {
            try
            {
                return new FileInputStream(resource);
            }
            catch (FileNotFoundException e)
            {
                return super.getResourceAsStream(name);
            }
        }
        return super.getResourceAsStream(name);
    }

    public File getClassFile(String className)
    {
        return ClassUtils.classNameToFile(_tempDir.getAbsolutePath(), className);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException
    {
        //check if our class exists in the tempDir

        if (_alreadyScanned.containsKey(className))
        {
            return _alreadyScanned.get(className);
        }

        File target = getClassFile(className);
        if (!target.exists())
        {
            return super.loadClass(className);
        }

        //ClassResource data = WeavingContext.getFileChangedDaemon().getClassMap().get(className);
        //if (data != null && !data.getRefreshAttribute().requiresRefresh()) {
        //    return data.getAClass();
        //}

        FileInputStream iStream = null;

        int fileLength;
        byte[] fileContent;
        try
        {
            fileLength = (int) target.length();
            fileContent = new byte[fileLength];
            iStream = new FileInputStream(target);
            int len = iStream.read(fileContent);
            if (_logger.isLoggable(Level.FINER))
            {
                _logger.log(Level.FINER, "class read {0} bytes read", String.valueOf(len));
            }
            //we have to do it here because just in case
            //a dependent class is loaded as well we run into classcast exceptions
            Class retVal = super.defineClass(className, fileContent, 0, fileLength);
            _alreadyScanned.put(className, retVal);
            return retVal;

        }
        catch (FileNotFoundException e)
        {
            throw new ClassNotFoundException(e.toString());
        }
        catch (IOException e)
        {
            throw new ClassNotFoundException(e.toString());
        }
        finally
        {
            if (iStream != null)
            {
                try
                {
                    iStream.close();
                }
                catch (Exception e)
                {
                    _logger.log(Level.SEVERE, "", e);
                }
            }
        }

    }

}

