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

package rewrite.org.apache.myfaces.extensions.scripting.core.engine;

import org.apache.myfaces.extensions.scripting.api.ClassScanListener;
import rewrite.org.apache.myfaces.extensions.scripting.core.common.ScriptingConst;
import rewrite.org.apache.myfaces.extensions.scripting.core.context.WeavingContext;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.api.ClassScanner;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.api.ScriptingEngine;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.StandardDependencyScanner;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.DependencyScanner;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.filter.WhitelistFilter;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.loaders.ScannerClassloader;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.registry.ExternalFilterDependencyRegistry;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class JavaDependencyScanner extends BaseScanner implements ClassScanner
{

    public JavaDependencyScanner() {
    }

    public int getEngineType() {
        return ScriptingConst.ENGINE_TYPE_JSF_JAVA;
    }
    public String getFileEnding() {
        return ScriptingConst.JAVA_FILE_ENDING;
    }

}
