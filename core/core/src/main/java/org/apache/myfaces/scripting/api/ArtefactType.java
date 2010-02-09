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
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A set of artifact types which are used internally
 *          so that the reloading strategies and other parts of the system
 *          can adapt to the type of the artifact which has to be reloaded
 */
@SuppressWarnings("unused")
public enum ArtefactType {
    MANAGED_BEAN,
    COMPONENT,
    BEHAVIOR,
    VALIDATOR,
    RENDERER,
    NAV_HANDLER,
    RENDERKIT,
    VIEWHANDLER,
    LIFECYCLE,
    FACESCONTEXT,
    ELRESOLVER,
    VARIABLERESOLVER,
    SCOPE,
    RESOURCEHANDLER
}
