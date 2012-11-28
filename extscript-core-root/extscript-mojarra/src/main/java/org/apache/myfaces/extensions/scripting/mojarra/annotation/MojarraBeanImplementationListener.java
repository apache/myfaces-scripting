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

package org.apache.myfaces.extensions.scripting.mojarra.annotation;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.annotation.AnnotationManager;
import org.apache.myfaces.extensions.scripting.core.api.AnnotationScanListener;
import org.apache.myfaces.extensions.scripting.jsf.annotation.BaseAnnotationScanListener;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class MojarraBeanImplementationListener extends BaseAnnotationScanListener implements AnnotationScanListener
{
    public boolean supportsAnnotation(String annotation)
    {
        return annotation.equals(ManagedBean.class.getName());
    }

    public boolean supportsAnnotation(Class annotation)
    {
        return annotation.equals(ManagedBean.class);
    }

    public void register(Class clazz, java.lang.annotation.Annotation ann)
    {

        javax.faces.bean.ManagedBean annCasted = (javax.faces.bean.ManagedBean) ann;

        //we need to reregister for every bean due to possible managed prop
        //and scope changes
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        AnnotationManager annotationManager = ApplicationAssociate.getInstance(context).getAnnotationManager();

        Set<Class> newType = new HashSet<Class>();
        newType.add(clazz);
        annotationManager.applyConfigAnnotations(FacesContext.getCurrentInstance(), ManagedBean.class, newType);
    }
}
