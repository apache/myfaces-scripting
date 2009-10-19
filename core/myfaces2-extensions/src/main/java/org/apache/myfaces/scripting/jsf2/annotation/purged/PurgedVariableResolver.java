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
package org.apache.myfaces.scripting.jsf2.annotation.purged;

import org.apache.myfaces.scripting.api.Decorated;

import javax.faces.el.VariableResolver;
import javax.faces.el.EvaluationException;
import javax.faces.context.FacesContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedVariableResolver extends VariableResolver implements Decorated {

    VariableResolver _delegate;

    public PurgedVariableResolver(VariableResolver delegate) {
        _delegate = delegate;
    }

    @Override
    public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException {
        throw new RuntimeException("VariableResolver does not exist");
    }

    public VariableResolver getDelegate() {
        return _delegate;
    }

    public void setDelegate(VariableResolver delegate) {
        _delegate = delegate;
    }
}
