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
package org.apache.myfaces.extensions.scripting.jsf2.annotation;

import org.apache.myfaces.extensions.scripting.api.AnnotationScanListener;
import org.apache.myfaces.extensions.scripting.jsf2.annotation.purged.PurgedComponent;

import javax.faces.component.FacesComponent;
import java.util.logging.Level;

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

    public boolean supportsAnnotation(Class annotation) {
        return annotation.equals(FacesComponent.class);  //To change body of implemented methods use File | Settings | File Templates.
    }


    protected void addEntity(Class clazz, String val) {
        if (_log.isLoggable(Level.FINEST)) {
            _log.log(Level.FINEST, "addComponent(" + val + "," + clazz.getName() + ")");
        }
        getApplication().addComponent(val, clazz.getName());
        //register the renderer if not registered

        _alreadyRegistered.put(clazz.getName(), val);
    }

    @Override
    public void purge(String className) {
        super.purge(className);
        //no purge needed we already have a different class
        //registered
        if (!_alreadyRegistered.containsKey(className)) {
            return;
        }
        String val = (String) _alreadyRegistered.remove(className);
        if (val != null) {
            getApplication().addComponent(val, PurgedComponent.class.getName());
        }
    }
}
