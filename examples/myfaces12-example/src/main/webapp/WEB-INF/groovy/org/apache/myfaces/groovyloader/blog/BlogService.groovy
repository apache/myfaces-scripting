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

import org.apache.myfaces.groovyloader.blog.BlogEntry;
import java.util.List
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory;



public class BlogService {
    List blogEntries = new ArrayList()

    public void addEntry2(BlogEntry entry) {
        Log log = LogFactory.getLog(BlogService.class)
        log.info("(BlogService.addEntry2): Adding entry")
        blogEntries << entry
    }

}