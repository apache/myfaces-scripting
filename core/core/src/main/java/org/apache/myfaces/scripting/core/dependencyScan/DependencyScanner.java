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

import org.apache.myfaces.scripting.core.dependencyScan.core.ClassScanUtils;
import org.apache.myfaces.scripting.core.dependencyScan.core.ClassScanVisitor;
import org.apache.myfaces.scripting.core.dependencyScan.core.ExtendedClassReader;
import org.apache.myfaces.scripting.core.dependencyScan.registry.ExternalFilterDependencyRegistry;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A dependency scanner for
 *          our classes
 *          <p /> this class is thread save on object level
 *          and can be used as a singleton
 *          <p/>
 */
public class DependencyScanner {
    final ClassScanVisitor _cp = new ClassScanVisitor();
    Logger _log = Logger.getLogger(this.getClass().getName());

    public DependencyScanner() {

    }

    public synchronized final void fetchDependencies(ClassLoader loader, String scanIdentifier, String className, ExternalFilterDependencyRegistry registry) {
        Set<String> retVal = new HashSet<String>();
        _cp.setScanIdentifier(scanIdentifier);

        investigateInheritanceHierarchy(loader, className, registry);
        registry.flush(scanIdentifier);
    }

    /**
     * @param className the class name of the class which has to be investigated for the code dependencies
     * @return a set of dependencies as string representation of their class names
     * @deprecated
     */
    public synchronized final Set<String> fetchDependencies(ClassLoader loader, String className, final Set<String> whiteList) {
        return null;
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
     * @param registry  the dependency registry
     */
    private void investigateInheritanceHierarchy(ClassLoader loader, String className, ExternalFilterDependencyRegistry registry) {
        //we now have to fetch the parent hierarchy

        try {
            Class toCheck = loader.loadClass(className);
            if (toCheck == null) {
                return;
            }
            scanCurrentClass(loader, className, registry);

            //we scan the hierarchy because we might have compiled-uncompiled-compiled connections, the same goes for the interfaces
            //the basic stuff can be covered by our class scanning but for more advanced usecase we have to walk the entire hierarchy per class!
            scanHierarchy(loader, toCheck, registry, true);
            //our asm code normally covers this but since the scanner has to work outside of asm we do it twice, the same goes for the hierarchy
            scanInterfaces(loader, toCheck, registry);
        } catch (ClassNotFoundException e) {
            _log.log(Level.SEVERE, "DefaultDependencyScanner.investigateInheritanceHierarchy() ", e);
        }
    }

    private void scanInterfaces(ClassLoader loader, Class toCheck, ExternalFilterDependencyRegistry registry) {
        Class[] interfaces = toCheck.getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            return;
        }

        for (Class currentInterface : interfaces) {
            if (ClassScanUtils.isStandardNamespace(currentInterface.getName())) {
                continue;
            }
            scanCurrentClass(loader, currentInterface.getName(), registry);

            //We scan also our parent interfaces to get a full coverage
            //but since interfaces do not implement anything we can cover
            //the parents
            scanHierarchy(loader, currentInterface, registry, false);
        }
    }

    /**
     * Scans the interface hierarchy of our class
     * the normal interface scan is processed already on class level
     * this method is needed to process our parent interface relationships
     * before triggering the ASM bytecode processing
     *
     * @param loader         the infrastructural classloader
     * @param toCheck        the class which needs to be checked
     * @param registry       the dependency registry
     * @param interfaceCheck if true also interfaces within the hierarchy will be processed, false if not
     */
    private void scanInterfaces(ClassLoader loader, Class toCheck, ExternalFilterDependencyRegistry registry, boolean interfaceCheck) {
        Class parent = toCheck.getSuperclass();

        while (parent != null && !ClassScanUtils.isStandardNamespace(parent.getName())) {
            if (interfaceCheck) {
                //we recursively descend into our interfaces
                scanInterfaces(loader, parent, registry);
            }

            scanCurrentClass(loader, parent.getName(), registry);
            parent = parent.getSuperclass();

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
     * @param registry       the dependency registry
     * @param interfaceCheck if true also interfaces within the hierarchy will be processed, false if not
     */
    private void scanHierarchy(ClassLoader loader, Class toCheck, ExternalFilterDependencyRegistry registry, boolean interfaceCheck) {
        Class parent = toCheck.getSuperclass();

        while (parent != null && !ClassScanUtils.isStandardNamespace(parent.getName())) {
            if (interfaceCheck) {
                //we recursively descend into our interfaces, it should not
                //get any cyclic calls the tainting mechanism should prevent that
                //and also the descension into parents, determinism should be
                //enabled by both measures
                //scanInterfaces(loader, retVal, whiteList, parent);
            }

            scanCurrentClass(loader, parent.getName(), registry);
            parent = parent.getSuperclass();

        }
    }

    /**
     * scans one level of the inheritance hierarchy
     *
     * @param loader           the classLoader which should be used for the hierarchy scanning
     * @param currentClassName the className which has to be investigated
     * @param registry         the dependency registry
     */
    private void scanCurrentClass(ClassLoader loader, String currentClassName, ExternalFilterDependencyRegistry registry) {
        _cp.setDependencyRegistry(registry);

        ClassReader cr;

        try {
            cr = new ExtendedClassReader(loader, currentClassName);
            cr.accept(_cp, 0);
        } catch (IOException e) {
            _log.log(Level.SEVERE, "scanCurrentClass() ", e);
        }
    }

}
