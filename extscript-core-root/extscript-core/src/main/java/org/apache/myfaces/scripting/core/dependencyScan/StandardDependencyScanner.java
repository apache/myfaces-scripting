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

import org.apache.myfaces.scripting.core.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.scripting.core.dependencyScan.api.DependencyScanner;
import org.apache.myfaces.scripting.core.dependencyScan.core.ClassScanUtils;
import org.apache.myfaces.scripting.core.dependencyScan.core.ClassScanVisitor;
import org.apache.myfaces.scripting.core.dependencyScan.core.ExtendedClassReader;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A dependency scanner for
 * our classes. This class is thread save on object level
 * and can be used as a singleton
 * <p/>
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class StandardDependencyScanner implements DependencyScanner {
    final ClassScanVisitor _cp = new ClassScanVisitor();
    Logger _logger = Logger.getLogger(this.getClass().getName());

    public StandardDependencyScanner() {

    }

    public synchronized final void fetchDependencies(ClassLoader loader, Integer engineType, String className, DependencyRegistry registry) {
        _cp.setEngineType(engineType);
        _cp.setRootClass(className);
        _cp.setDependencyRegistry(registry);
        investigateInheritanceHierarchy(loader, className);
        registry.flush(engineType);
    }

    /**
     * this investigates the classes inheritance hierarchy for
     * more dependencies, for now annotations and interfaces
     * are omitted since they are not vital to our jsf dependency checks
     * (maybe in the long run we will add interfaces and annotations as well
     * but for now we will leave them away for speed reasons)
     *
     * @param loader    the classLoader which should be used for the hierarchy scanning
     * @param className the className which has to be investigated
     */
    private void investigateInheritanceHierarchy(ClassLoader loader, String className) {
        //we now have to fetch the parent hierarchy

        try {
            Class toCheck = loader.loadClass(className);
            if (toCheck == null) {
                return;
            }
            scanCurrentClass(loader, className);

            //we scan the hierarchy because we might have compiled-uncompiled-compiled connections, the same goes for the interfaces
            //the basic stuff can be covered by our class scanning but for more advanced usecase we have to walk the entire hierarchy per class!
            scanHierarchy(loader, toCheck);
            //our asm code normally covers this but since the scanner has to work outside of asm we do it twice, the same goes for the hierarchy
            scanInterfaces(loader, toCheck);
        } catch (ClassNotFoundException e) {
            _logger.log(Level.SEVERE, "DefaultDependencyScanner.investigateInheritanceHierarchy() ", e);
        }
    }

    private void scanInterfaces(ClassLoader loader, Class toCheck) {
        Class[] interfaces = toCheck.getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            return;
        }

        for (Class currentInterface : interfaces) {
            if (ClassScanUtils.isStandardNamespace(currentInterface.getName())) {
                continue;
            }
            scanCurrentClass(loader, currentInterface.getName());

            //We scan also our parent interfaces to get a full coverage
            //but since interfaces do not implement anything we can cover
            //the parents
            scanHierarchy(loader, currentInterface);
        }
    }

    /**
     * scans the parent child relationship hierarchy
     * We have to go through the entire hierarchy except for standard
     * namespaces due to the fact that we have to cover source <->binary<->source
     * dependencies with binary being binary classes never to be refreshed
     * <p/>
     * Note we can optionally do some interface checks here
     * for now annotations are only processed by the class scanner itself
     * so we do not process any annotation inheritance on this level
     * we will add the feature later
     *
     * @param loader         the infrastructural classloader
     * @param toCheck        the class which needs to be checked
     */
    private void scanHierarchy(ClassLoader loader, Class toCheck) {
        Class parent = toCheck.getSuperclass();

        while (parent != null && !ClassScanUtils.isStandardNamespace(parent.getName())) {
            scanCurrentClass(loader, parent.getName());
            parent = parent.getSuperclass();
        }
    }

    /**
     * scans one level of the inheritance hierarchy
     *
     * @param loader           the classLoader which should be used for the hierarchy scanning
     * @param currentClassName the className which has to be investigated
     */
    private void scanCurrentClass(ClassLoader loader, String currentClassName) {
        ClassReader cr;
        try {
            cr = new ExtendedClassReader(loader, currentClassName);
            cr.accept(_cp, 0);
        } catch (IOException e) {
            _logger.log(Level.SEVERE, "scanCurrentClass() ", e);
        }
    }

}
