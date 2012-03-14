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
package org.apache.myfaces.extensions.scripting.core.dependencyScan.registry;

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.api.ClassFilter;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.core.ClassDependencies;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.filter.ScanIdentifierFilter;
import org.apache.myfaces.extensions.scripting.core.dependencyScan.filter.StandardNamespaceFilter;
import org.apache.myfaces.extensions.scripting.core.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * registry facade which is used to track our dependencies
 */
public class DependencyRegistryImpl implements ExternalFilterDependencyRegistry {
    List<ClassFilter> _filters = new LinkedList<ClassFilter>();

    ClassDependencies _dependencMap;

    //private volatile Strategy _registrationStrategy;
    final Integer _engineType;

    /**
     * constructor for our facade
     *
     * @param engineType    the engine type this registry should support
     * @param dependencyMap the dependency map which stores the dependencies
     */
    public DependencyRegistryImpl(Integer engineType, ClassDependencies dependencyMap) {
        _dependencMap = dependencyMap;
        _engineType = engineType;

        _filters.add(new ScanIdentifierFilter(_engineType, ScriptingConst.ENGINE_TYPE_JSF_ALL, ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE));
        _filters.add(new StandardNamespaceFilter());
    }

    /**
     * Clears the entire filter map
     */
    public void clearFilters() {
        _filters.clear();
        _filters.add(new ScanIdentifierFilter(_engineType, ScriptingConst.ENGINE_TYPE_JSF_ALL, ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE));
        _filters.add(new StandardNamespaceFilter());
    }

    /**
     * adds a new filter
     *
     * @param filter the filter to be added
     */
    public void addFilter(ClassFilter filter) {
        _filters.add(filter);
    }

    /**
     * checks if the className is allowed in the current filter chain
     *
     * @param engineType an identifier for the current scan type (jsf java scan for instance)
     * @param className  the classname to be checked
     * @return true if a filter triggers false if not
     */
    public boolean isAllowed(Integer engineType, String className) {
        for (ClassFilter filter : _filters) {
            if (!filter.isAllowed(_engineType, className)) {
                return false;
            }
        }
        return true;
    }

    /**
     * adds a dependency to our dependency map (usually rootclass -> dependency and currentClass -> dependency)
     *
     * @param engineType            the engine type for this dependency
     * @param rootClass             the root class of this scan which all dependencies are referenced from
     * @param currentlyVisitedClass the source which includes or casts the dependencies
     * @param dependency            the dependency to be added
     */
    public void addDependency(Integer engineType, String rootClass, String currentlyVisitedClass, String dependency) {

        if (StringUtils.isBlank(dependency)) {
            return;
        }

        if (currentlyVisitedClass != null && currentlyVisitedClass.equals(dependency)) {
            return;
        }



        if (!isAllowed(engineType, dependency)) {
            return;
        }

        //not needed
        //if(!StringUtils.isBlank(currentlyVisitedClass)) {
        //    _dependencMap.addDependency(currentlyVisitedClass, dependency);
        //}

        //for now we code it into a list like we used to do before
        //but in the long run we have to directly register
        //to save one step
        //getDependencySet(source).add(dependency);
        if(!StringUtils.isBlank(rootClass)) {
            _dependencMap.addDependency(rootClass, dependency);
        }
    }

    /**
     * flush to flush down our stored dependencies into our final map
     */
    public void flush(Integer engineType) {
        //_registrationStrategy.apply(_dependencies);
    }

}
