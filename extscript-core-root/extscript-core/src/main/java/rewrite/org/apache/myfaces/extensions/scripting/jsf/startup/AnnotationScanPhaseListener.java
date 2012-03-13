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

package rewrite.org.apache.myfaces.extensions.scripting.jsf.startup;

import rewrite.org.apache.myfaces.extensions.scripting.core.context.WeavingContext;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * This phase listener is needed because the annotation scanner
 * relies on the facesContext to be present, we cannot do without it
 */
public class AnnotationScanPhaseListener implements PhaseListener
{

    @Override
    public void afterPhase(PhaseEvent event)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void beforePhase(PhaseEvent event)
    {
        FacesContext context = event.getFacesContext();
        Map<Object, Object> params = context.getAttributes();
        if(params.containsKey("ANN_PROCESSED")) return;
        else params.put("ANN_PROCESSED", Boolean.TRUE);
        WeavingContext.getInstance().annotationScan();
    }

    @Override
    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
