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
package org.apache.myfaces.groovyloader.core

import org.apache.myfaces.extensions.scripting.api.BaseWeaver
import org.apache.myfaces.extensions.scripting.core.reloading.SimpleReloadingStrategy

public class StandardGroovyReloadingStrategy extends SimpleReloadingStrategy {

  public StandardGroovyReloadingStrategy(BaseWeaver weaver) {
    super(weaver);
  }

  public StandardGroovyReloadingStrategy() {
    super();
  }

  /**
   * central algorithm which determines which property values are overwritten and which are not
   */
  protected void mapProperties(def target, def src) {
    src.properties.each {property ->
      //ok here is the algorithm, basic datatypes usually are not copied but read in anew and then overwritten
      //later on
      //all others can be manually overwritten by adding an attribute <attributename>_changed

      try {
        if (target.properties.containsKey(property.key)
                && !property.key.equals("metaClass")        //the class information and meta class information cannot be changed
                && !property.key.equals("class")            //otherwise we will get following error
                // java.lang.IllegalArgumentException: object is not an instance of declaring class
                && !(
        target.properties.containsKey(property.key + "_changed") //||
        //nothing further needed the phases take care of that
        )) {
          target.setProperty(property.key, property.value)
        }
      } catch (Exception e) {

      }
    }
  }
}

