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
package rewrite.org.apache.myfaces.extensions.scripting.core.api;

import java.lang.annotation.Annotation;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          We use a source code artifact observer here to register the
 *          meta data in the correct registry entries
 */

public interface AnnotationScanListener extends ClassScanListener
{

    /**
     * returns true if the annotation marked by the incoming parameter is supported by this scanner
     *
     * @param annotation the supported annotation as neutral string representation of its class
     * @return in case of support
     */
    public boolean supportsAnnotation(String annotation);

    /**
     * returns true if the annotation marked by the incoming parameter is supported by this scanner
     *
     * @param annotation the supported annotation as neutral string representation of its class
     * @return in case of support
     */
    public boolean supportsAnnotation(Class annotation);


    /**
     * Class file registration of the supported annotation
     *
     * @param clazz          the class to be registered
     * @param annotationName the annotation for the class
     */
    public void register(Class clazz, Annotation annotationName);

    /**
     * Purges the class from the correct places of the myfaces registry
     * so that the artifact is not reachable anymore
     *
     * @param className the class name for the class which needs to be purged
     */
    public void purge(String className);
}
