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
package org.apache.myfaces.scripting.jsf2.annotation;

import com.thoughtworks.qdox.model.JavaClass;
import org.apache.myfaces.scripting.api.AnnotationScanListener;
import org.apache.myfaces.scripting.jsf2.annotation.purged.PurgedComponent;

import javax.faces.component.FacesComponent;
import java.util.Map;
import java.util.Iterator;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ComponentImplementationListener extends SingleEntityAnnotationListener implements AnnotationScanListener {

    public ComponentImplementationListener() {
        super();
        _entityParamValue = "value";
    }


    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesComponent.class.getName());  //To change body of implemented methods use File | Settings | File Templates.
    }


    protected void addEntity(Class clazz, String val) {
        if (log.isTraceEnabled()) {
            log.trace("addComponent(" + val + ","
                      + clazz.getName() + ")");
        }
        getApplication().addComponent(val, clazz.getName());
        _alreadyRegistered.put(val, clazz.getName());
    }

    protected void addEntity(JavaClass clazz, String val) {
        if (log.isTraceEnabled()) {
            log.trace("addComponent(" + val + ","
                      + clazz.getFullyQualifiedName() + ")");
        }
        getApplication().addComponent(val, clazz.getFullyQualifiedName());
        _alreadyRegistered.put(val, clazz.getFullyQualifiedName());
    }


    @Override
    public void purge(String className) {
        super.purge(className);
        Iterator<Map.Entry<String, Object>> it = _alreadyRegistered.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String storedClassName = (String) entry.getValue();
            if (className.equals(storedClassName)) {
                //this is a recursive call into getApplication().addComponent we have to
                //our application has to respond to this by blocking additional scans
                //if the add component already has been called and is not finished
                getApplication().addComponent(entry.getKey(), PurgedComponent.class.getName());
                it.remove();
            }
        }
    }
}
