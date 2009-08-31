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
package org.apache.myfaces.scripting.loaders.groovy;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;

/**
 * TODO check if this is deprecated
 *
 * @author Werner Punz
 */
public class MetaclassStubcompilerFix extends DelegatingMetaClass {

    public MetaclassStubcompilerFix(Class aClass) {
        super(aClass);
        initialize();
    }

    public MetaclassStubcompilerFix(MetaClass metaClass) {
        super(metaClass);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /*dummy constructor do not use it it bypasses
  * a bug in the maven-groovy stub compiler regarding
  * base classes*/
    public MetaclassStubcompilerFix() {
        super(MetaclassStubcompilerFix.class);
    }
}
