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
package org.apache.myfaces.groovyloader.blog;

import javax.faces.context.FacesContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory;

/**
 * Utils class to keep the code clean and mean
 */
public class JSFUtil {


  
    public static Object resolveVariable(String beanName) {
        Log log = LogFactory.getLog(JSFUtil.class)
        log.info("ElResolver Instance:" + FacesContext.getCurrentInstance().getELContext().getELResolver().toString())
        return FacesContext.getCurrentInstance().getELContext().getELResolver().getValue(FacesContext.getCurrentInstance().getELContext(), null, beanName)
    }

}