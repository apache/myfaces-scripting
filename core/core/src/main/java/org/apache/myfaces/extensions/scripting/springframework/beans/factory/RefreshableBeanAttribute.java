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
package org.apache.myfaces.extensions.scripting.springframework.beans.factory;

import org.springframework.core.Conventions;

/**
 * <p>An attribute that will be attached to those bean definitions that are refreshable, i.e. this
 * class contains some of the required meta data that you'll have to access if you want to decide
 * whether a bean definition has to be refreshed or not. Note that this attribute, however, is
 * only capable of determining whether a bean definition has to be refreshed, sometimes you'll
 * actually have to refresh bean instances as well.</p>
 *
 */
public class RefreshableBeanAttribute {

    /**
     * <p>The name of the bean definition meta data attribute that refers to an instance
     * of this class, i.e. use this attribute name if you want to obtain all information
     * regarding the last time that the bean definition has been refreshed.</p>
     */
    public static final String REFRESHABLE_BEAN_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(RefreshableBeanAttribute.class, "refreshableBeanAttribute");

    /**
     * <p>The timestamp in milliseconds of the last time that the bean
     * definition that this attribute belongs to has been requested to
     * refresh itself.</p>
     */
    private long refreshRequested;

    /**
     * <p>The timestamp in milliseconds of the last time that the bean
     * definition that this attribute belongs to has been actually
     * refreshed.</p>
     */
    private long refreshExecuted;

    /**
     * <p>By calling this method the user is able to request another refresh. Note that
     * this doesn't cause the bean factory to refresh the bean definition immediately,
     * but rather it just signals a request. The bean definition will be refreshed once
     * the bean factory has to deal with the next bean request (i.e. a call to
     * getBean()).</p>
     */
    public void requestRefresh() {
        refreshRequested = System.currentTimeMillis();
    }

    /**
     * <p>Returns the timestamp in milliseconds of the last time that a refresh operation
     * has been requested.</p>
     *
     * @return the timestamp in milliseconds of the last refresh request
     */
    public long getRequestedRefreshDate() {
        return refreshRequested;
    }

    /**
     * <p>By calling this method the user indicates that the according bean definition
     * has just been refreshed, which means that the method #{@link #requiresRefresh()}
     * will return <code>false</code> until the user requests the next refresh.</p>
     */
    public void executedRefresh() {
        refreshExecuted = System.currentTimeMillis();
    }

    /**
     * <p>Returns the timestamp in milliseconds of the last time that a refresh operation
     * has been executed.</p>
     *
     * @return the timestamp in milliseconds of the last executed refresh operation
     */
    public long getExecutedRefreshDate() {
        return refreshExecuted;
    }

    /**
     * <p>Determines whether a refresh is required, i.e. whether the user has requested
     * another refresh operation by calling {@link #requestRefresh()} recently. Note that
     * a call to this method only determines whether the bean definition on its own has
     * to be refreshed (i.e. it doesn't even consider a particular bean instance).</p>
     *
     * @return whether a refresh call is required
     */
    public boolean requiresRefresh() {
        return getExecutedRefreshDate() < getRequestedRefreshDate();
    }

}
