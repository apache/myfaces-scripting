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
package org.apache.myfaces.scripting.core.util;

/**
 * helper class which allows to deal with loops which have to keep track
 * of two running loop variables one being the total counter
 * the other one being a counter which is modulated
 */
public class ExtendedLoopCnt {
    int _cnt = 0;
    int _totalCnt = 0;
    int _delimiter = 0;

    public ExtendedLoopCnt(int cnt, int totalCnt, int cntDelimiter) {
        _cnt = cnt;
        _totalCnt = totalCnt;
        _delimiter = cntDelimiter;
    }

    public final int getCnt() {
        return _cnt;
    }

    public final int getTotalCnt() {
        return _totalCnt;
    }

    public final int incCnt() {
        _cnt = (_cnt + 1) % _delimiter;
        return _cnt;
    }

    public final int incTotalCnt() {
        _totalCnt++;
        return _totalCnt;
    }
}
