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
package org.apache.myfaces.scripting.core.dependencyScan;

import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Utils which store the shared code
 */
class ClassScanUtils {
    public static final String BINARY_PACKAGE = "\\/";

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

    /**
     * checks for an allowed namespaces from a given namespace list
     * if the class or package is in the list of allowed namespaces
     * a true is returned otherwise a false
     *
     * @param classOrPackage the class or package name to be checked for allowance
     * @param nameSpaces     the list of allowed namespaces
     * @return true if the namespace is within the boundaries of the whitelist false otherwise
     */
    @SuppressWarnings("unused")
    public static boolean allowedNamespaces(String classOrPackage, String[] nameSpaces) {

        //ok this is probably the fastest way to iterate hence we use this old construct
        //a direct or would be faster but we cannot do it here since we are not dynamic here
        int len = nameSpaces.length;
        for (int cnt = 0; cnt < len; cnt++) {
            if (classOrPackage.startsWith(nameSpaces[cnt])) {
                return true;
            }
        }
        return false;
    }

    /**
     * renames the internal member class descriptors of L<qualified classnamewith />; to its source name
     *
     * @param internalClassName the internal class name
     * @return the changed classname in its sourceform
     */
    public static String internalClassDescriptorToSource(String internalClassName) {
        //we strip the meta information which is not needed
        //aka start with ( strip all to )

        //()means usually beginning of a native type
        if (internalClassName.startsWith("(")) {
            internalClassName = internalClassName.substring(internalClassName.lastIndexOf(')') + 1);
        }

        //()I for single data types
        if (internalClassName.equals("") || internalClassName.length() == 1) {
            return null;
        }

        //fully qualified name with meta information
        if (internalClassName.endsWith(";")) {
            //we can skip all the other meta information, a class identifier on sub class level
            //has to start with L the format is <META ATTRIBUTES>L<CLASS QUALIFIED NAME>;
            //The meta attributes can be for instance [ for array
            internalClassName = internalClassName.substring(internalClassName.indexOf('L') + 1, internalClassName.length() - 1);
        }

        //normal fully qualified name with no meta info attached
        internalClassName = internalClassName.replaceAll(BINARY_PACKAGE, ".");
        return internalClassName;
    }

    /**
     * logs a dependency if it does not belong to the standard namespaces
     * and also it only is added if it belongs to our whitelist of
     * non standard namespaces (the standard check is just a short circuiting
     * for performance reasons, before going into the heavier whitelist
     * namespace check)
     *
     * @param dependencies the target which has to receive the dependency in source format
     * @param whiteList    the whitelist of allowed dependencies
     * @param parameters   the list of dependencies which have to be added
     */
    public static void logParmList(Collection<String> dependencies, final Set<String> whiteList, final String... parameters) {
        for (String singleParameter : parameters) {
            if (singleParameter == null) continue;
            if (singleParameter.equals("")) continue;
            singleParameter = internalClassDescriptorToSource(singleParameter);
            if (singleParameter == null || isStandardNamespace(singleParameter)) continue;

            String[] packages = singleParameter.split("\\.");

            StringBuilder fullPackage = null;
            for (String currPackage : packages) {
                if (fullPackage != null) {
                    fullPackage.append(".");
                    fullPackage.append(currPackage);
                } else {
                    fullPackage = new StringBuilder(singleParameter.length());
                    fullPackage.append(currPackage);
                }

                String tempPackage = fullPackage.toString();
                if (whiteList.contains(tempPackage)) {
                    dependencies.add(singleParameter);
                    break;
                }
            }
        }
    }
}
