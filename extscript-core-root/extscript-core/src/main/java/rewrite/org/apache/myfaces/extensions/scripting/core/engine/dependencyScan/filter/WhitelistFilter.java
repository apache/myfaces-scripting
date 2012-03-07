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
package rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.filter;

import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.ClassFilter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter class which depends upon a list of whitelisted packages
 * wildcards in this filter are implicit which means
 * <p/>
 * org.apache.myfaces includes all files
 * under org.apache.myfaces
 */
public class WhitelistFilter implements ClassFilter
{

    WhiteListNode _whiteList = new WhiteListNode();

    /*we use a package tree here to make the whitelist check as performant as possible*/

    class WhiteListNode {
        Map<String, WhiteListNode> _value = new ConcurrentHashMap<String, WhiteListNode>();

        public WhiteListNode addEntry(String key) {
            if (_value.containsKey(key)) {
                return _value.get(key);
            }
            WhiteListNode retVal = new WhiteListNode();
            _value.put(key, retVal);
            return retVal;
        }

        public boolean hasChildren() {
            return !_value.isEmpty();
        }

        public Map<String, WhiteListNode> getValue() {
            return _value;
        }

        public void setValue(Map<String, WhiteListNode> value) {
            this._value = value;
        }

        public WhiteListNode get(String key) {
            return _value.get(key);
        }
    }

    public WhitelistFilter(String... whiteList) {
        for (String singlePackage : whiteList) {
            addEntry(singlePackage);
        }
    }

    public WhitelistFilter(Collection<String> whiteList) {
        for (String singlePackage : whiteList) {
            addEntry(singlePackage);
        }
    }

    /**
     * whitespace is allowed implementation
     *
     * @param engineType integer value of the engine type of the class
     * @param clazz      the class itself to be processed by the filter
     * @return true if it is white-spaced, false otherwise
     */
    public final boolean isAllowed(Integer engineType, String clazz) {
        String[] subParts = clazz.split("\\.");
        WhiteListNode currPackage = _whiteList;
        WhiteListNode parentPackage = null;
        for (String subPart : subParts) {
            currPackage = currPackage.get(subPart);
            if (isRootPackageMismatch(currPackage, parentPackage)) {
                return false;
            } else if (isSubpackage(currPackage, parentPackage)) {
                return true;
            } else if (isMismatch(currPackage)) {
                return false;
            }

            parentPackage = currPackage;
        }
        return true;
    }

    private void addEntry(String singlePackage) {
        String[] subPackages = singlePackage.split("\\.");
        WhiteListNode currPackage = _whiteList;
        for (String subPackage : subPackages) {
            currPackage = currPackage.addEntry(subPackage);
        }
    }

    //special conditions extracted for readability reasons in the core
    //algorithm

    private boolean isMismatch(WhiteListNode currPackage) {
        return currPackage == null;
    }

    private boolean isSubpackage(WhiteListNode currPackage, WhiteListNode parentPackage) {
        return currPackage == null && parentPackage != null && !parentPackage.hasChildren();
    }

    private boolean isRootPackageMismatch(WhiteListNode currPackage, WhiteListNode parentPackage) {
        return currPackage == null && parentPackage == null;
    }
}
