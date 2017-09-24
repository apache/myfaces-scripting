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

package org.apache.myfaces.extensions.scripting.core.monitor;

import java.io.File;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p>&nbsp;</p>
 *          An abstraction on our class watcher
 *          we now deal with generic resources to simplify the access
 */

public abstract class WatchedResource implements Cloneable {

    /**
     * tainted override for dependend classes
     */
    volatile boolean tainted = false;

    //volatile long  _lastLoaded = -1L;

    /**
     * Unique identifier on the resource
     *
     * @return get the unique idientifier for this resource
     */
    public abstract String getIdentifier();

    /**
     * @return a file handle on the current resource
     */
    public abstract File getFile();

    public WatchedResource getClone()  {
        try {
            return (WatchedResource) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public boolean isTainted()
    {
        return tainted;
    }

    public void setTainted(boolean tainted)
    {
        this.tainted = tainted;
    }
}
