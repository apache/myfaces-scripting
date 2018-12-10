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
package blog;

import java.util.logging.Logger;

import static blog.JSFUtilJava.resolveVariable;

/**
 * Our Blog View controller
 * (note we have a dummy annotation here for testing purposes)
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@DependencyTestAnnotation
public class Blog {

    
    String title = "<h3>Hello to the MyFaces Dynamic Blogging Example</h3>";
    String title1 = "You can alter the code for this small blogging application on the fly, " +
            "you even can add new classes on the fly and Java will pick it up";

    String firstName = "";
    String lastName = "";
    String topic = "";

    String content = "";

    private Logger getLog() {
        return Logger.getLogger(this.getClass().getName());
    }

    public String addEntry() {
        getLog().info("adding entry");
        
        /*important we have an indirection over an interface here*/
        BlogServiceInterface service = (BlogServiceInterface) resolveVariable("javaBlogService");

        if (service == null) {
            getLog().severe("service not found");
        } else {
            getLog().fine("service found");
        }

        BlogEntry entry = new BlogEntry();
        //we now map it in the verbose way, the lean way would be to do direct introspection attribute mapping

        entry.setFirstName(firstName);
        entry.setLastName(lastName);
        entry.setTopic(topic);
        
        entry.setContent(content);

        if (service != null) {
            /*convenience method to call a method on an object dynamically
            * executeMethod and cast are static imports which encapsulates the
            * ugly stuff the java introspection provides and reduce
            * the loc down to sane levels
            *
            * note the behavior in case of calling errors
            * is changed from the default managed behavior
            * to an unmanaged behavior. This is mostly
            * the same behavior you get from scripting engines!
            * 
            */

            //include for presentation 3
            //entry.setTopic(debuggingTest());
              
            service.addEntry(entry);
        }

        //we stay on the same page
        return null;
    }

    //include for presentation 3
    /*public String debuggingTest() {
        return "Debugging Topic set via dynamic code";
    }*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

  

}
