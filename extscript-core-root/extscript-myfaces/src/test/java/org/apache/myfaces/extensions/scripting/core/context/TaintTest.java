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

package org.apache.myfaces.extensions.scripting.core.context;

import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.engine.FactoryEngines;
import org.apache.myfaces.extensions.scripting.core.support.ContextUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@Ignore
public class TaintTest
{
    FactoryEngines factory = null;

    public TaintTest(FactoryEngines factory)
    {
        this.factory = factory;

    }

    public void init() throws IOException
    {
        ContextUtils.startupSystem();
    }

    @Test
    public void taintTest()
    {
        //we sleep 1000 to let the system play catchup
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {

        }
        WeavingContext.getInstance().forName("Test");
        WeavingContext.getInstance().forName("TestJava");
    }

}
