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
package org.apache.myfaces.extensions.scripting.api;

/**
 * Generic class scanner interface
 * which is a helper to plug in external  scanners
 * as adapters for the annotation and dependency handling
 * we cannot deal with annotations directly in the core
 * because we are bound by the jsf 1.2 lower threshold limit
 * hence this indirection
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface ClassScanner {

    public void scanPaths();

    @SuppressWarnings("unused")
    public void clearListeners();

    @SuppressWarnings("unused")
    public void addListener(ClassScanListener listener);

    public void addScanPath(String scanPath);

    @SuppressWarnings("unchecked")
    public void scanClass(Class clazz);

    public void scanAndMarkChange();
}
