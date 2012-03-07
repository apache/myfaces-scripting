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
package rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.core;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Utils which store the shared code
 */
public class ClassScanUtils {

    private static final String DOMAIN_JAVA = "java.";
    private static final String DOMAIN_JAVAX = "javax.";
    private static final String DOMAIN_COM_SUN = "com.sun";
    private static final String DOMAIN_APACHE = "org.apache.";
    private static final String DOMAIN_MYFACES = "org.apache.myfaces";
    private static final String DOMAIN_JBOSS = "org.jboss";
    private static final String DOMAIN_SPRING = "org.springframework";
    private static final String DOMAIN_JUNIT = "org.junit";
    private static final String DOMAIN_ECLIPSE = "org.eclipse";
    private static final String DOMAIN_NETBEANS = "org.netbeans";
    private static final String DOMAIN_GROOVY = "groovy.";
    private static final String DOMAIN_SCALA = "scala.";
    private static final String DOMAIN_JYTHON = "jython.";
    private static final String DOMAIN_JRUBY = "jruby.";

    /**
     * checks if a given package or class
     * belongs to a standard namespaces which is
     * untouchable by an implementer
     *
     * @param in the page or fully qualified classname
     * @return true if it belongs to one of the standard namespaces, false if not
     */
    public static boolean isStandardNamespace(String in) {
        //We don't use a regexp here, because an test has shown that direct startsWith is 5 times as fast as applying
        //a precompiled regexp with match

        //shortcuts for a faster killing of the add before going into the heavier
        //whitelist check, this one kills off classes which belong to standard
        //and semi standard namespaces before whitelisting the rest
        return in.startsWith(DOMAIN_JAVA) ||
                in.startsWith(DOMAIN_JAVAX) ||
                in.startsWith(DOMAIN_COM_SUN) ||
                in.startsWith(DOMAIN_GROOVY) ||
                in.startsWith(DOMAIN_JYTHON) ||
                in.startsWith(DOMAIN_JRUBY) ||
                in.startsWith(DOMAIN_SCALA) ||
                in.startsWith(DOMAIN_JBOSS) ||
                in.startsWith(DOMAIN_SPRING) ||
                in.startsWith(DOMAIN_JUNIT) ||
                in.startsWith(DOMAIN_ECLIPSE) ||
                in.startsWith(DOMAIN_NETBEANS) ||

                //apache domain has to be treated specially myfaces can be referenced due to our tests and demos, otherwise this one
                //is also treated as taboo zone
                ((in.startsWith(DOMAIN_APACHE) &&
                        !in.startsWith(DOMAIN_MYFACES)));
    }
   
}
