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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter class which depends upon a list of whitelisted packages
 */
public class WhitelistFilter implements ClassFilter {

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

    public WhitelistFilter(List<String> whiteList) {
        for (String singlePackage : whiteList) {
            addEntry(singlePackage);
        }
    }

    private void addEntry(String singlePackage) {
        String[] subPackages = singlePackage.split("\\.");
        WhiteListNode currPackage = _whiteList;
        for (String subPackage : subPackages) {
            currPackage = currPackage.addEntry(subPackage);
        }
    }

    public boolean isAllowed(String clazz) {
        String[] subParts = clazz.split("\\.");
        WhiteListNode currPackage = _whiteList;
        for (String subPart : subParts) {
            currPackage = currPackage.get(subPart);
            if (currPackage == null) {
                return false;
            }
        }
        return true;
    }
}
