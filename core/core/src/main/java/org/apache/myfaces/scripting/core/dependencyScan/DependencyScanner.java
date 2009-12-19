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

package org.apache.myfaces.scripting.core.dependencyScan;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 * 
 * A dependency scanner for
 * our classes
 */
public class DependencyScanner {

    static final ClassScanVisitor cp = new ClassScanVisitor();

    /**
     * @param className
     * @return
     */
    public static final Set<String> fetchDependencies(String className) {
        Set<String> retVal = new HashSet<String>();

        cp.setDependencyTarget(retVal);
        ClassReader cr = null;

        try {
            cr = new ClassReader("org.apache.myfaces.extensions.scripting.dependencyScan.probes.Probe");
            cr.accept(cp, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retVal;
    }

}
