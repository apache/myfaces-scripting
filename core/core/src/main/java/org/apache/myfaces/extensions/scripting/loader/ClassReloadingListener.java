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
package org.apache.myfaces.extensions.scripting.loader;

import org.apache.myfaces.extensions.scripting.loader.support.ThrowAwayClassLoader;

/**
 * <p>Implement this interface if you want to get a notification when a class gets reloaded (or
 * loaded for the first time). Register your implementation afterwards using
 * {@link org.apache.myfaces.extensions.scripting.loader.ReloadingClassLoader#registerReloadingListener(ClassReloadingListener)}.</p> 
 *
 * @author Bernhard Huemer
 */
public interface ClassReloadingListener {

    /**
     * <p>Callback method that will be called if the reloading class loader, that
     * you've attached this listener to, has either reloaded a class or loaded a
     * class for the first time (use the given class loaders to distinguish
     * between those two cases).</p>
     *
     * @param oldClassLoader the old class loader that has been replaced by the new one, or
     *                          <code>null</code> if the given class has been loaded for the first time
     * @param newClassLoader the new class loader that has replaced the old one
     * @param className the name of the class that has been reloaded
     */
    public void classReloaded(
            ThrowAwayClassLoader oldClassLoader, ThrowAwayClassLoader newClassLoader, String className);    

}
