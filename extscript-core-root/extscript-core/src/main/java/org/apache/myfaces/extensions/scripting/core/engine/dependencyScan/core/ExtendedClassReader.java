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
package org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.core;

import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * Class reader for ASM which allows to plug our own loader instead
 * of the default one
 * <p>&nbsp;</p>
 * (ASM makes too many assumptions regarding the loader)
 */
public class ExtendedClassReader extends ClassReader {
    /**
     * classloader pluggable classreader
     *
     * @param loader    the loader which has to be plugged into the system
     * @param className the class name for the class which has to be investigated
     * @throws java.io.IOException in case of a loading error (class cannot be loaded for whatever reason)
     */
    public ExtendedClassReader(ClassLoader loader, String className) throws IOException {
        super(loader.getResourceAsStream(className.replace('.', '/')
                + ".class"));
    }

}
