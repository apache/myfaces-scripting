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
public class DefaultDependencyScanner implements DependencyScanner {

    final ClassScanVisitor cp = new ClassScanVisitor();
    Logger log = Logger.getLogger(this.getClass().getName());

    public DefaultDependencyScanner() {
    }

    /**
     * @param className the class name of the class which has to be investigated for the code dependencies
     * @return a set of dependencies as string representation of their class names
     */
    public synchronized final Set<String> fetchDependencies(ClassLoader loader, String className, final Set<String> whiteList) {
        Set<String> retVal = new HashSet<String>();
        investigateInheritanceHierarchy(loader, retVal, className, whiteList);
        return retVal;
    }

    /**
     * this investigates the classes inheritance hierarchy for
     * more dependencies, for now annotations and interfaces
     * are omitted since they are not vital to our jsf dependency checks
     * (maybe in the long run we will add interfaces and annotations as well
     * but for now we will leave them away for speed reasons)
     *
     * @param loader    the classLoader which should be used for the hierarchy scanning
     * @param retVal    the receiving set
     * @param className the className which has to be investigated
     * @param whiteList the package scanning whitelist
     */
    private void investigateInheritanceHierarchy(ClassLoader loader, Set<String> retVal, String className, Set<String> whiteList) {
        //we now have to fetch the parent hierarchy

        try {
            Class toCheck = loader.loadClass(className);
            if (toCheck == null) {
                return;
            }
            scanCurrentClass(loader, retVal, className, whiteList);

            //we scan the hierarchy because we might have compiled-uncompiled-compiled connections, the same goes for the interfaces
            //the basic stuff can be covered by our class scanning but for more advanced usecase we have to walk the entire hierarchy per class!
            scanHierarchy(loader, retVal, whiteList, toCheck, true);
            //our asm code normally covers this but since the scanner has to work outside of asm we do it twice, the same goes for the hierarchy
            scanInterfaces(loader, retVal, whiteList, toCheck);
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "DefaultDependencyScanner.investigateInheritanceHierarchy() ", e);
        }
    }

    private void scanInterfaces(ClassLoader loader, Set<String> retVal, Set<String> whiteList, Class toCheck) {
        Class[] interfaces = toCheck.getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            return;
        }

        for (Class currentInterface : interfaces) {
            if (ClassScanUtils.isStandardNamespace(currentInterface.getName())) {
                continue;
            }
            scanCurrentClass(loader, retVal, currentInterface.getName(), whiteList);

            //We scan also our parent interfaces to get a full coverage
            //but since interfaces do not implement anything we can cover
            //the parents
            scanHierarchy(loader, retVal, whiteList, currentInterface, false);
        }

    }

    /**
     * scans the hierarchy of a given class
     *
     * @param loader
     * @param retVal
     * @param whiteList
     * @param toCheck
     */
    private void scanHierarchy(ClassLoader loader, Set<String> retVal, Set<String> whiteList, Class toCheck, boolean interfaceCheck) {
        Class parent = toCheck.getSuperclass();

        while (parent != null && !ClassScanUtils.isStandardNamespace(parent.getName())) {
            if (interfaceCheck) {
                //we recursively descend into our interfaces, it should not
                //get any cyclic calls the tainting mechanism should prevent that
                //and also the descension into parents, determinism should be
                //enabled by both measures
                //scanInterfaces(loader, retVal, whiteList, parent);
            }

            scanCurrentClass(loader, retVal, parent.getName(), whiteList);
            parent = parent.getSuperclass();

        }
    }

    /**
     * scans one level of the inheritance hierarchy
     *
     * @param loader           the classLoader which should be used for the hierarchy scanning
     * @param retVal           the receiving set
     * @param currentClassName the className which has to be investigated
     * @param whiteList        the package scanning whitelist
     */
    private void scanCurrentClass(ClassLoader loader, Set<String> retVal, String currentClassName, Set<String> whiteList) {
        cp.setDependencyTarget(retVal);
        cp.setWhiteList(whiteList);
        ClassReader cr;

        try {
            cr = new ExtendedClassReader(loader, currentClassName);
            cr.accept(cp, 0);
        } catch (IOException e) {
            log.log(Level.SEVERE, "scanCurrentClass () ", e);
        }
    }

}
