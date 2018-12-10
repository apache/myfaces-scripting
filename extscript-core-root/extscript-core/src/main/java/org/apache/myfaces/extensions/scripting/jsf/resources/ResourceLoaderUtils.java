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
package org.apache.myfaces.extensions.scripting.jsf.resources;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ResourceLoaderUtils {

    public final static String JAVAX_FACES_LIBRARY_NAME = "javax.faces";
    public final static String JSF_JS_RESOURCE_NAME = "jsf.js";

    public final static String MYFACES_JS_RESOURCE_NAME = "oamSubmit.js";
    public final static String MYFACES_JS_RESOURCE_NAME_UNCOMPRESSED = "oamSubmit-uncompressed.js";

    public final static String MYFACES_LIBRARY_NAME = "org.apache.myfaces";
    private final static String RENDERED_MYFACES_JS = "org.apache.myfaces.RENDERED_MYFACES_JS";

    // TODO: In tomcat and jetty it is implemented a Flyweight pattern when converting
    // date headers. For now it is better keep this stuff simple.
    private static final String HTTP_RESPONSE_DATE_HEADER =
            "EEE, dd MMM yyyy HH:mm:ss zzz";

    private static final String[] HTTP_REQUEST_DATE_HEADER = {
            "EEE, dd MMM yyyy HH:mm:ss zzz", "EEEEEE, dd-MMM-yy HH:mm:ss zzz",
            "EEE MMMM d HH:mm:ss yyyy"};

    private static TimeZone __GMT = TimeZone.getTimeZone("GMT");

    public static String formatDateHeader(long value) {
        SimpleDateFormat format = new SimpleDateFormat(
                HTTP_RESPONSE_DATE_HEADER,
                Locale.US);
        format.setTimeZone(__GMT);
        return format.format(new Date(value));
    }

    public static Long parseDateHeader(String value) {
        Date date = null;
        for (int i = 0; (date == null) && (i < HTTP_REQUEST_DATE_HEADER.length); i++) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(
                        HTTP_REQUEST_DATE_HEADER[i], Locale.US);
                format.setTimeZone(__GMT);
                date = format.parse(value);
            } catch (ParseException e) {
                ;
            }
        }
        if (date == null) {
            return null;
        }
        return new Long(date.getTime());
    }

    //Taken from trinidad URLUtils
    public static long getResourceLastModified(URL url) throws IOException {
        if ("file".equals(url.getProtocol())) {
            String externalForm = url.toExternalForm();
            // Remove the "file:"
            File file = new File(externalForm.substring(5));

            return file.lastModified();
        } else {
            return getResourceLastModified(url.openConnection());
        }
    }

    //Taken from trinidad URLUtils
    public static long getResourceLastModified(URLConnection connection) throws IOException {
        long modified;
        if (connection instanceof JarURLConnection) {
            // The following hack is required to work-around a JDK bug.
            // getLastModified() on a JAR entry URL delegates to the actual JAR file
            // rather than the JAR entry.
            // This opens internally, and does not close, an input stream to the JAR
            // file.
            // In turn, you cannot close it by yourself, because it's internal.
            // The work-around is to get the modification date of the JAR file
            // manually,
            // and then close that connection again.

            URL jarFileUrl = ((JarURLConnection) connection).getJarFileURL();
            URLConnection jarFileConnection = jarFileUrl.openConnection();

            try {
                modified = jarFileConnection.getLastModified();
            } finally {
                try {
                    jarFileConnection.getInputStream().close();
                } catch (Exception exception) {
                    // Ignored
                }
            }
        } else {
            modified = connection.getLastModified();
        }

        return modified;
    }
}
