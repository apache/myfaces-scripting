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
package org.apache.myfaces.extensions.scripting.jsf.resources;

/**
 * Contains the metadata information to reference a resource 
 * 
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 946779 $ $Date: 2010-05-20 15:31:42 -0500 (Jue, 20 May 2010) $
 */
public abstract class ResourceMeta
{
    
    public abstract String getLibraryName();
    
    public abstract String getResourceName();

    public abstract String getLocalePrefix();

    public abstract String getLibraryVersion();

    public abstract String getResourceVersion();
    
    public abstract String getResourceIdentifier();
    
    public abstract boolean couldResourceContainValueExpressions();
}
