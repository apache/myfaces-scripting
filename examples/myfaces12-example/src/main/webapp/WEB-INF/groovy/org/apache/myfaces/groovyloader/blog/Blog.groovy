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
package org.apache.myfaces.groovyloader.blog

import org.apache.myfaces.groovyloader.blog.JSFUtil;
import org.apache.myfaces.groovyloader.blog.BlogService
import java.util.logging.Logger;



public class Blog {
  //bug application and session scoped beans  are not refreshed structurally yet

  Logger log = Logger.getLogger(Blog.class.getName())

  String title = "Hello to the myfaces dynamic blogging Groovy JSF 1.2 Blog "
  String title1 = """\
                    You can alter the code for this small blogging application on the fly,
                    you even can add new classes on the fly and Grooy will pick it up
                    (you can use every groovy feature as untyped references or multiline comments)
                    """

  String firstName = ""
  String lastName = ""
  String topic = ""

  String content = ""


  public String addEntry() {
    log.info("adding entry");

    def service = JSFUtil.resolveVariable("blogService")

    if (service == null) {
      log.severe("service not found")
    } else {
      log.info("service found")

    }

    BlogEntry entry = new BlogEntry()
    //we now map it in the verbose way, the lean way would be to do direct introspection attribute mapping

    entry.firstName = firstName
    entry.lastName = lastName
    entry.topic = topic
    entry.content = content


    service.addEntry2(entry)

    //we stay on the same page
    return null;
  }

}