/*
 *  Copyright 2010 werpu2.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.apache.myfaces.blank;

import javax.faces.application.Application;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * A startup listener which suppresses
 * the ext-val warning (which actually is a bug in ext-val)
 * for version 2.0.3, is already reported and will be fixed in the
 * long run
 * 
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class SuppressExtValWarningStartupListener implements SystemEventListener {
    public boolean isListenerForSource(Object source) {
        return source instanceof Application;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void processEvent(SystemEvent event) {
        if (!event.getClass().equals(PostConstructApplicationEvent.class)) {
            return;
        }
        
        //TODO disable the EXT-VAL warning
    }   

}
