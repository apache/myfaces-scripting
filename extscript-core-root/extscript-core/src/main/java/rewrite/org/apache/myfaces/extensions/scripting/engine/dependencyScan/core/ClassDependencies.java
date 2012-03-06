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
package rewrite.org.apache.myfaces.extensions.scripting.engine.dependencyScan.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class dependency maps
 * note this class is thread save
 */
public class ClassDependencies {

    /**
     * reverse index which shows which
     * a class name and which classes in the system depend on that
     * classname
     * <p/>
     * <p/>
     * the key is a dependency a class has the _value is a set of classes which depend on the current class
     */
    private Map<String, Set<String>> reverseIndex = new ConcurrentHashMap<String, Set<String>>();

    public void addDependency(String referencingClass, String referencedClass) {
        Set<String> reverseDependencies = getReverseDependencies(referencedClass);
        reverseDependencies.add(referencingClass);
    }

    /**
     * adds a set of dependencies to the
     * reverse lookup index
     *
     * @param referencingClass  the referencing class of this dependency
     * @param referencedClasses the referenced class of this dependency
     */
    public void addDependencies(String referencingClass, Collection<String> referencedClasses) {
        for (String referencedClass : referencedClasses) {
            addDependency(referencingClass, referencedClass);
        }
    }

    /**
     * removes a referenced class an all its referencing classes!
     *
     * @param clazz the referenced class to be deleted
     */
    public void removeReferenced(String clazz) {
        reverseIndex.remove(clazz);
    }

    /**
     * removes a referencing class
     * and deletes the referenced
     * entry if it is not referenced anymore
     *
     * @param clazz the referencing class to delete
     */
    @SuppressWarnings("unused")
    public void removeReferrer(String clazz) {
        List<String> emptyReferences = new ArrayList<String>(reverseIndex.size());
        for (Map.Entry<String, Set<String>> entry : reverseIndex.entrySet()) {
            Set<String> entrySet = entry.getValue();
            entrySet.remove(clazz);
            if (entrySet.isEmpty()) {
                emptyReferences.add(entry.getKey());
            }
        }
        for (String toDelete : emptyReferences) {
            removeReferenced(toDelete);
        }
    }

    public Set<String> getReferringClasses(String referencedClass) {
        return reverseIndex.get(referencedClass);
    }

    private Set<String> getReverseDependencies(String dependency) {
        Set<String> dependencies = reverseIndex.get(dependency);
        if (dependencies == null) {
            dependencies = Collections.synchronizedSet(new HashSet<String>());
            reverseIndex.put(dependency, dependencies);
        }
        return dependencies;
    }

}
