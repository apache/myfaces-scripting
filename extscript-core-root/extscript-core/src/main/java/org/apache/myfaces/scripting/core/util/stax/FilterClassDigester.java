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

package org.apache.myfaces.scripting.core.util.stax;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * A web.xml filter class digester based on StaX
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class FilterClassDigester {
    private static final String ATTR_FILTER_CLASS = "filter-class";

    public static boolean findFilter(URL webxml, Class filterClass) {
        StringBuilder filterClassFound = new StringBuilder();
        Set<String> filterClasses = new HashSet<String>();
        XMLStreamReader parser = null;
        try {
            InputStream in = webxml.openStream();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLStreamReader(in);
            boolean inFilter = false;
            for (int event = parser.next();
                 event != XMLStreamConstants.END_DOCUMENT;
                 event = parser.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        if (isFilterClass(parser.getLocalName())) {
                            inFilter = true;
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (isFilterClass(parser.getLocalName())) {
                            inFilter = false;
                            if (filterClassFound.toString().equals(filterClass.getName())) {
                                filterClasses.add(filterClassFound.toString());
                                return true;
                            }
                            filterClassFound = new StringBuilder();
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (inFilter) filterClassFound.append(parser.getText());
                        break;
                } // end switch
            } // end while

        }
        catch (XMLStreamException ex) {
            return false;
        }
        catch (IOException ex) {
            return false;
        } finally {
            try {
                parser.close();
            } catch (XMLStreamException e) {
               return false;
            }
        }

        return false;
    }

    private static boolean isFilterClass(String name) {
        if (name.equals(ATTR_FILTER_CLASS)) return true;
        return false;
    }

}
