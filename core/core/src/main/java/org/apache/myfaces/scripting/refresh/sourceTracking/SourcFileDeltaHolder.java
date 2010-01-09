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
package org.apache.myfaces.scripting.refresh.sourceTracking;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          We need additonal data structures which keep track of
 *          the current state of the source files, to be able
 *          to deal with annotations in a decent way
 *          <p/>
 *          For annotations we have to trigger the compiler as soon
 *          as a resolver runs into a class not exists situation
 */

public class SourcFileDeltaHolder {

    public static final int STATE_INITIAL = 1;
    public static final int STATE_ADDED = 2;
    public static final int STATE_DELETED = 3;


    /**
     * state holder
     */
    SourceFilesetStateHolder _initialState = new SourceFilesetStateHolder();
    SourceFilesetStateHolder _added = new SourceFilesetStateHolder();
    SourceFilesetStateHolder _deleted = new SourceFilesetStateHolder();

    public SourceFilesetState getFilesetState(int state, Integer engineType) {
        SourceFilesetState retVal = null;
        switch (state) {
            case STATE_INITIAL:
                return getFileSetState(engineType, _initialState);

            case STATE_ADDED:
                return getFileSetState(engineType, _added);

            case STATE_DELETED:
                return getFileSetState(engineType, _deleted);

            default:
                break;
        }

        return null;
    }

    private SourceFilesetState getFileSetState(Integer engineType, SourceFilesetStateHolder holder) {
        SourceFilesetState retVal = null;
        retVal = _initialState.get(engineType);
        if (retVal == null) {
            retVal = new SourceFilesetState();
            _initialState.put(engineType, retVal);
        }
        return retVal;
    }


    public SourceFilesetStateHolder getInitialState() {
        return _initialState;
    }

    public void setInitialState(SourceFilesetStateHolder initialState) {
        _initialState = initialState;
    }

    public SourceFilesetStateHolder getAdded() {
        return _added;
    }

    public void setAdded(SourceFilesetStateHolder added) {
        _added = added;
    }

    public SourceFilesetStateHolder getDeleted() {
        return _deleted;
    }

    public void setDeleted(SourceFilesetStateHolder deleted) {
        _deleted = deleted;
    }
}
