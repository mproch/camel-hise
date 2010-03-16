/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.camel.component.hise;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.hise.api.HISEEngine;
import org.apache.hise.engine.store.HumanInteractionsCompiler;
import org.apache.hise.lang.HumanInteractions;
import org.apache.hise.lang.TaskDefinition;
import org.springframework.core.io.Resource;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mproch
 * Date: Mar 12, 2010
 * Time: 9:05:17 PM
 */
public class HiseComponent extends DefaultComponent {

    private HISEEngine hiseEngine;

    private HumanInteractions humanInteractions;

    @Override
    protected Endpoint createEndpoint(String s, String s1, Map<String, Object> stringObjectMap) throws Exception {
        QName id = new QName(s1.substring(0, s1.lastIndexOf("/")), s1.substring(s1.lastIndexOf("/")+1));
        if (hiseEngine == null) {
            hiseEngine = getCamelContext().getRegistry().lookup("hiseEngine", HISEEngine.class);
        }
        return new HiseEndpoint(s, this, getTaskDefinition(id), hiseEngine);
    }

    TaskDefinition getTaskDefinition(QName id) throws Exception {
        if (humanInteractions == null) {
            Resource humanInteractionsResource = getCamelContext().getRegistry().lookup("humanInteractionsResource", Resource.class);
            humanInteractions = HumanInteractionsCompiler.compile(humanInteractionsResource);
        }
        if (!humanInteractions.getTaskDefinitions().containsKey(id)) {
            throw new Exception(id+" not defined, defined tasks are: "+humanInteractions.getTaskDefinitions().keySet());
        }
        return humanInteractions.getTaskDefinitions().get(id);
    }

    public void setHiseEngine(HISEEngine hiseEngine) {
        this.hiseEngine = hiseEngine;
    }

    public void setHumanInteractions(HumanInteractions humanInteractions) {
        this.humanInteractions = humanInteractions;
    }
}
