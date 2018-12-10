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
package blog;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;


/**
 * @author werpu2
 * @date: 01.09.2009
 * <p/>
 * A helper for JSF and introspection related tasks
 */
public class JSFUtilJava {

    public JSFUtilJava() {
    }

    /**
     * resolves a variable in the current facesContext
     *
     * @param beanName
     * @return
     */
    public static Object resolveVariable(String beanName) {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        ELContext elContext = facesContext.getELContext();
        ELResolver elResolver =elContext.getELResolver();

        return elResolver.getValue(elContext, null, beanName);
    }

}
