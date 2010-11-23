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

import javax.el.ELResolver;
import javax.el.ELContext;
import java.util.Iterator;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedELResolver extends ELResolver implements Decorated {

    private final String DOES_NOT_EXIST = "EL Resolver does not exist";

    ELResolver _delegate;

    public PurgedELResolver(ELResolver delegate) {
        _delegate = delegate;
    }

    @Override
    public Object getValue(ELContext elContext, Object o, Object o1) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Class getType(ELContext elContext, Object o, Object o1) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void setValue(ELContext elContext, Object o, Object o1, Object o2) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public boolean isReadOnly(ELContext elContext, Object o, Object o1) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Iterator getFeatureDescriptors(ELContext elContext, Object o) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Class getCommonPropertyType(ELContext elContext, Object o) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    public ELResolver getDelegate() {
        return _delegate;
    }
}
