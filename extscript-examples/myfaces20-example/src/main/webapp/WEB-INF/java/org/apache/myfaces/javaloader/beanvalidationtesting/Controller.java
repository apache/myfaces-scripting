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

package org.apache.myfaces.javaloader.beanvalidationtesting;

import org.apache.myfaces.extensions.validator.beanval.annotation.BeanValidation;
import org.apache.myfaces.extensions.validator.beanval.annotation.ModelValidation;
import org.apache.myfaces.javaloader.beanvalidationtesting.group.Admin;
import org.apache.myfaces.javaloader.beanvalidationtesting.group.Name;
import org.apache.myfaces.javaloader.beanvalidationtesting.group.User;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.validation.groups.Default;



/**
 * Controller class which is triggered and uses the EXT-VAL validation
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

@ManagedBean(name = "validationController")
@RequestScoped
public class Controller {

    
    @BeanValidation.List({
            @BeanValidation(useGroups = Default.class),
            @BeanValidation(viewIds = "/beanValidation.xhtml", useGroups = User.class),
            @BeanValidation(viewIds = "/groupValidation02.jsp", useGroups = Admin.class),
            @BeanValidation(viewIds = "/modelValidation01.jsp", useGroups = Admin.class),
            @BeanValidation(viewIds = "/modelValidation01.jsp", useGroups = Name.class,
                    modelValidation = @ModelValidation(isActive = true))
    })
    private Person person = new Person();



    public String validateSubmit() {
        System.out.println("Validating submit");
        // System.out.println(FacesContext.getCurrentInstance().getViewRoot().getViewId());
        return null;
    }


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }



}
