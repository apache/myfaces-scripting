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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
import javax.faces.validator.Validator;
import javax.faces.validator.FacesValidator;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@FacesValidator(value = "at.irian.CustomValidator")
public class TestValidator2 implements Validator {
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
   
        if(!((String)value).trim().equals("hello world")) {
            LogFactory.getLog(TestValidator1.class).error("validation failed");
            throw new ValidatorException(new FacesMessage("validation failed from validator 1 please input hello world, original input" + ((String)value),"validation failed from validator 1 please input hello world, original input" + ((String)value)));
        }
    }
}
