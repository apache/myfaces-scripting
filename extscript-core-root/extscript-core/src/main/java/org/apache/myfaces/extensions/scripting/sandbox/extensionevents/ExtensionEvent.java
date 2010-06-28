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

package org.apache.myfaces.extensions.scripting.sandbox.extensionevents;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * Extension event class.
 *
 * The idea is that extensions attach themselves
 * over an event/messaging system to achieve loose coupling
 * between the core and an extension.
 *
 * Extensions will get following events.
 *
 * ScanRecommended
 * CompileRecommended (for their own compilation directories)
 * UpdateConfigurationRecommended
 *
 */

public class ExtensionEvent {

    public ExtensionEvent() {
    }

}
