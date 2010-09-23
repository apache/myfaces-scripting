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
import org.apache.myfaces.extensions.scripting.jsf2.annotation.purged.PurgedBehavior;

import javax.faces.component.behavior.FacesBehavior;
import java.util.logging.Level;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class BehaviorImplementationListener extends SingleEntityAnnotationListener implements AnnotationScanListener {

    public BehaviorImplementationListener() {
        super();
        _entityParamValue = "value";
    }

    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(FacesBehavior.class.getName());
    }

    public boolean supportsAnnotation(Class annotation) {
        return annotation.equals(FacesBehavior.class);
    }


    protected void addEntity(Class clazz, String val) {
        if (_log.isLoggable(Level.FINEST)) {
            _log.log(Level.FINEST, "addBehavior(" + val + ","
                    + clazz.getName() + ")");
        }
        getApplication().addBehavior(val, clazz.getName());
    }

    @Override
    public void purge(String className) {
        super.purge(className);
        if (!_alreadyRegistered.containsKey(className)) {
            return;
        }

        String val = (String) _alreadyRegistered.remove(className);
        if (val != null) {
            getApplication().addBehavior(val, PurgedBehavior.class.getName());
        }
    }

}
