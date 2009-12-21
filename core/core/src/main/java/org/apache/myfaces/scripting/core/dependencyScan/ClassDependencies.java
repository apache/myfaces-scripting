package org.apache.myfaces.scripting.core.dependencyScan;

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
     * the key is a depenency a class has the value is a set of classes which depend on the current class
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
     * @param referencingClass
     * @param referencedClasses
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
    public void removeReferencing(String clazz) {
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


    public Set<String> getReferencedClasses(String referencingClass) {
        return reverseIndex.get(referencingClass);
    }

    private final Set<String> getReverseDependencies(String dependency) {
        Set<String> dependencies = reverseIndex.get(dependency);
        if (dependencies == null) {
            dependencies = Collections.synchronizedSet(new HashSet<String>());
            reverseIndex.put(dependency, dependencies);
        }
        return dependencies;
    }

}
