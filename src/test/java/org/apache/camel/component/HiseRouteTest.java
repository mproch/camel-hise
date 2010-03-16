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

package org.apache.camel.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hise.api.HISEEngine;
import org.apache.hise.api.Handler;
import org.apache.hise.api.Sender;
import org.apache.hise.dao.HISEDao;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: mproch
 * Date: Mar 13, 2010
 * Time: 10:47:37 AM
 */
public class HiseRouteTest extends RouteBuilder implements HISEEngine {

    DefaultCamelContext ctx;

    Sender sender;

    QName taskRegistered;

    String task = "http://www.insurance.example.com/claims/Task1";

    public void registerTask(TaskInfo taskInfo) {
        taskRegistered = taskInfo.taskDefinition.getTaskName();
    }

    public Node receive(Handler handler, QName qName, String s, Element element, Node node) {
        element.setTextContent("pops");
        sender = (Sender) handler;
        return element;
    }

    public HISEDao getHiseDao() {
        return null;  
    }

    @Override
    public void configure() throws Exception {
        from("hise:"+task).to("log:fromHise","mock:fromHise");

        from("direct:toHise").to("hise:"+task).to("log:hiseAnswer", "mock:hiseAnswer");
    }

    @Before
    public void before() throws Exception {
        ctx = new DefaultCamelContext();
        SimpleRegistry r = new SimpleRegistry();
        r.put("hiseEngine", this);
        r.put("humanInteractionsResource", new ClassPathResource("/testHtd1.xml"));

        ctx.setRegistry(r);
        ctx.addRoutes(this);
    }
    
    @Test
    public void simpleTest() throws Exception {
        ctx.start();

        ctx.createProducerTemplate().sendBody("direct:toHise","<aaa/>");
        assertEquals(QName.valueOf("{http://www.insurance.example.com/claims}Task1"), taskRegistered);
        MockEndpoint ha = ctx.getEndpoint("mock:hiseAnswer", MockEndpoint.class);
        ha.expectedMessageCount(1);
        ha.assertIsSatisfied();

        Node input = getContext().getTypeConverter().convertTo(Node.class, "<answer/>");
        sender.invoke(input, null);
        MockEndpoint fh = ctx.getEndpoint("mock:fromHise", MockEndpoint.class);
        fh.expectedMessageCount(1);
        fh.assertIsSatisfied();
        
    }


}
