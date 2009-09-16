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
package org.apache.myfaces.scripting;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Annotation;
import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;
import org.junit.Ignore;

import java.io.File;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Testing ground for qdox evaluation
 *
 *
 * we set it to ignore because it is a testing
 * 
 * class to evaluate how to handle qdox
 * on java sources
 */

@Ignore
public class QDoxTest {

    //TODO replace this with maven specific dirs
    static final String sourceProbe = "/Users/werpu2/development/workspace/extensions-scripting3/core/myfaces2-extensions/src/test/java/org/apache/myfaces/scripting/Probe.java";

    private JavaSource[] scanIt() throws IOException {
        JavaDocBuilder _builder = new JavaDocBuilder();
        _builder.addSource(new File(sourceProbe));
        JavaSource[] sources = _builder.getSources();

        return sources;

    }

    //@Test
    public void probeSource() {

        try {
            JavaSource[] sources = scanIt();

            boolean managedBeanFound = false;
            for (JavaSource source : sources) {
                JavaPackage pckg = source.getPackage();
                assertTrue(pckg.getName().equals("org.apache.myfaces.scripting"));
                JavaClass[] classes = source.getClasses();
                for (JavaClass clazz : classes) {
                    Annotation[] annotations = clazz.getAnnotations();
                    for (Annotation annot : annotations) {
                        System.out.println(annot.toString());
                        managedBeanFound |= annot.getType().getValue().equals("javax.faces.bean.ManagedBean");
                        if(managedBeanFound) {
                            break;
                        }
                    }
                }
            }

            assertTrue(managedBeanFound);

        } catch (IOException e) {
            fail(e.toString());
        }

    }

}
