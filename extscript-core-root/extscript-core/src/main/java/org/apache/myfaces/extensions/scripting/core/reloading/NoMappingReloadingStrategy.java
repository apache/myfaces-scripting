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
package org.apache.myfaces.extensions.scripting.core.reloading;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          <p/>
 *          The renderer is a stateless flyweight pattern the reloading strategy is
 *          to do nothing, this should give optimal results
 *          <p/>
 *          <p/>
 *          The components are a similar case they are not flyweight
 *          but the properties usually are preserved by the lifecycle if possible
 *          or assigned by the tag handlers
 *          <p/>
 *          <p/>
 *          The same also applies to other flyweight patterned classes
 *          like converters or validators
 *          <p/>
 *          <p/>
 *          The only ones which need to keep some state are the ones
 *          which keep delegates, like the NavHandler
 */
public class NoMappingReloadingStrategy extends SimpleReloadingStrategy
{

    public NoMappingReloadingStrategy()
    {
        super();
    }

    @Override
    protected void mapProperties(Object target, Object src)
    {
    }
}
