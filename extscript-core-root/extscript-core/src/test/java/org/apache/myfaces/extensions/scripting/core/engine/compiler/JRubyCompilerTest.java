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

package org.apache.myfaces.extensions.scripting.core.engine.compiler;

import org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import org.junit.Test;

import java.io.File;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class JRubyCompilerTest
{
    private static final String ROOT_DIR =
            "/Users/werpu2/development/workspace/extscript_trunk/extscript-core-root/extscript-core";
    private static final String PROBE1 = "src/test/resources/compiler/TestProbe1.rb";
    private static final String PROBE2 = "src/test/resources/compiler/TestProbe2.rb";
    private static final String RESOURCES = "src/test/resources/";

    File probe1;
    File probe2;
    File root;

    private static final String RESULT_HAS_NO_ERRORS = "result has no errors";
    private static final String TARGET_DIR_EXISTS = "targetDir exists files are compiled into the targetDir";
    private static final String CLASSFILE1_IS_COMPILED = "Classfile1 is compiled into the targetDir";
    private static final String CLASSFILE1_IS_COMPILED1 = "Classfile1 is compiled into the target";
    private static final String CLASSFILE2_IS_COMPILED = "Classfile2 is compiled into the target";

    @Test
    public void basicCompilerTest()
    {
        File tempDir = FileUtils.getTempDir();
        tempDir.mkdirs();
        JRubyCompiler compiler = new JRubyCompiler();
        compiler.compile(new File(ROOT_DIR), tempDir, File.separator + PROBE1);
    }

}
