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

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.hise.api.HISEEngine;
import org.apache.hise.api.Handler;
import org.apache.hise.api.Sender;
import org.apache.hise.engine.store.TaskDD;
import org.apache.hise.lang.TaskDefinition;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: mproch
 * Date: Mar 12, 2010
 * Time: 9:18:36 PM
 */
public class HiseEndpoint extends DefaultEndpoint implements Sender, Handler {

    private TaskDefinition definition;

    private HISEEngine hiseEngine;

    private HiseConsumer consumer;

    public HiseEndpoint(String endpointUri, Component component, TaskDefinition definition, HISEEngine hiseEngine) {
        super(endpointUri, component);
        this.definition = definition;
        this.hiseEngine = hiseEngine;
        registerTask();
    }

    public Producer createProducer() throws Exception {
        return new HiseProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        if (consumer != null) {
            throw new Exception("Only one consumer allowed");
        }
        consumer = new HiseConsumer(this, processor);
        return consumer;
    }

    public boolean isSingleton() {
        return true;
    }
    
    public Node invoke(Node node, Node node1) {
        if (consumer == null) {
            throw new RuntimeException("No consumer specified for "+definition.getTaskName());
        }
        return consumer.consume(node, node1);
    }

    public String getId() {
        return null;
    }

    private void registerTask() {
        TaskDD dd = new TaskDD();
        dd.setHandler(this);
        dd.setSender(this);
        dd.setTaskName(definition.getTaskName());
        HISEEngine.TaskInfo ti = new HISEEngine.TaskInfo();
        ti.taskDefinition = definition;
        ti.dd = dd;
        hiseEngine.registerTask(ti);
    }

    TaskDefinition getDefinition() {
        return definition;
    }

    HISEEngine getHiseEngine() {
        return hiseEngine;
    }

}
