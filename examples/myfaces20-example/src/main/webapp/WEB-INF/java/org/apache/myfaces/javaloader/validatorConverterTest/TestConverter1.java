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
package org.apache.myfaces.javaloader.validatorConverterTest;

import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@FacesConverter(value="at.irian.CustomConverter")
public class TestConverter1 implements Converter {
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        return "hello from converter1";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        return "hello from converter1";  //To change body of implemented methods use File | Settings | File Templates.
    }
}