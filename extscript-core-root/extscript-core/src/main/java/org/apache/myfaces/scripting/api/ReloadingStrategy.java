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
package org.apache.myfaces.scripting.api;

/**
 * <p/>
 * Generic strategy for reloading
 * this should encapsulate various
 * reloading strategies
 * which have to be applied depending
 * on the artifact
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface ReloadingStrategy {
    /**
     * Reload method which is the central point for this
     * strategy pattern
     *
     * @param toReload     the object to be reloaded
     * @param artifactType the artifact type to be reloaded (so that the pattern either can ignore it or use it)
     * @return either the original or the reloaded artifact depending on its type and state
     */
    public Object reload(Object toReload, int artifactType);

    /**
     * Now this looks weird, but some scripting languages
     * have problems in a mixed environment so we allow
     * the calling weaver to be set lazily
     *
     * @param weaver the calling weaver to be set
     */
    public void setWeaver(ScriptingWeaver weaver);

    /**
     * getter for completeness
     *
     * @return the calling weaver
     */
    public ScriptingWeaver getWeaver();
}
