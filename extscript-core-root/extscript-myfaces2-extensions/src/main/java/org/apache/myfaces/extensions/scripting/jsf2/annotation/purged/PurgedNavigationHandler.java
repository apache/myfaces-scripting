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
package org.apache.myfaces.extensions.scripting.jsf2.annotation.purged;

import org.apache.myfaces.extensions.scripting.api.Decorated;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedNavigationHandler extends NavigationHandler implements Decorated {

    NavigationHandler _delegate;

    public PurgedNavigationHandler(NavigationHandler delegate) {
        _delegate = delegate;
    }

    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        throw new RuntimeException("Navigation handler does not exist");
    }

    public NavigationHandler getDelegate() {
        return _delegate;
    }

    public void setDelegate(NavigationHandler delegate) {
        _delegate = delegate;
    }
}
