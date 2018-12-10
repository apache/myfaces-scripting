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

package org.apache.myfaces.extensions.scripting.scanningcore.support;

import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import org.apache.myfaces.extensions.scripting.core.monitor.ResourceMonitor;
import org.apache.myfaces.extensions.scripting.scanningcore.support.MockServletContext;

import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ContextUtils
{
    /**
         * A startup routine shared by many tests
         * to do the basic weaving initialization
         *
         * @return the mockup servlet context
         */
        public static MockServletContext startupSystem() {
            MockServletContext context = new org.apache.myfaces.extensions.scripting.scanningcore.support.MockServletContext();

            WeavingContext wcontext = WeavingContext.getInstance();
            try
            {
                wcontext.initEngines();
                wcontext.getConfiguration().init(context);
                ResourceMonitor.init(context);
                //TODO ??
                ResourceMonitor.getInstance().performMonitoringTask();
            }
            catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            //WeavingContextInitializer.initWeavingContext(context);
            return context;
        }
}
