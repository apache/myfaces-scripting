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
package org.apache.myfaces.extensions.scripting.jsf.annotation.purged;



import org.apache.myfaces.extensions.scripting.core.api.Decorated;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedLifecycle extends Lifecycle implements Decorated
{

    private static final String DOES_NOT_EXIST = "Lifecycle does not exist";

    Lifecycle _delegate;

    public PurgedLifecycle(Lifecycle delegate) {
        _delegate = delegate;
    }

    @Override
    public void addPhaseListener(PhaseListener listener) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void execute(FacesContext context) throws FacesException {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public PhaseListener[] getPhaseListeners() {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void removePhaseListener(PhaseListener listener) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void render(FacesContext context) throws FacesException {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    public Object getDelegate() {
        return _delegate;
    }
}
