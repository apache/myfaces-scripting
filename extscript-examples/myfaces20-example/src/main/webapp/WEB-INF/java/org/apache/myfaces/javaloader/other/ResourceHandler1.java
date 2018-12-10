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
package org.apache.myfaces.javaloader.other;


import org.apache.myfaces.extensions.scripting.core.api.Decorated;

import javax.faces.application.ResourceHandler;
import javax.faces.application.Resource;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ResourceHandler1 extends BaseResourceHandler implements Decorated
{
    public ResourceHandler1(ResourceHandler delegate) {
        super(delegate);
    }

    public ResourceHandler1() {
    }

    @Override
    public Resource createResource(String resourceName) {
        if (resourceName.equals("testResource")) {
            Resource retVal = new StringResource("hello world from resource handler1, you can change me on the fly, " +
                    "but you must activate me first in the faces-config.xml file");
            return retVal;
        }
        return super.createResource(resourceName);
    }


    @Override
    public Object getDelegate() {
        return super.getDelegate();

    }
}
