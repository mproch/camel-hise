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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hise.lang.xsd.htd.TTaskInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: mproch
 * Date: Mar 12, 2010
 * Time: 9:19:13 PM
 */
public class HiseProducer extends DefaultProducer {

    private Log logger = LogFactory.getLog(HiseProducer.class);

    //private HiseEndpoint hiseEndpoint;

    public HiseProducer(HiseEndpoint endpoint) {
        super(endpoint);
        
        if (endpoint == null) {
            throw new NullPointerException("Null endpoint passed.");
        }
    }
    
    public void process(Exchange exchange) throws Exception {
        
        String body = exchange.getIn().getBody(String.class);
        
        //TODO: fixxx w HISE required
        Document bodyToSend = getEndpoint().getCamelContext().getTypeConverter().convertTo(Document.class, "<wrap>" + body + "</wrap>");
        TTaskInterface def = getHiseEndpoint().getDefinition().getTaskInterface();

        //TODO!
        Node createdBy = getEndpoint().getCamelContext().getTypeConverter().convertTo(Node.class,
                createCreatedBy(exchange.getProperty(HiseEndpoint.CREATED_BY_PROPERTY, "", String.class)));
        logger.debug("Sending "+body+" to hise with wrap: "+bodyToSend+"task: "+getHiseEndpoint().getDefinition().getTaskName());
        
        Node response = getHiseEndpoint().getHiseEngine().receive(getHiseEndpoint(), def.getPortType(), def.getOperation(), bodyToSend.getDocumentElement(), createdBy);
        exchange.getOut().setBody(response);
    }

    private String createCreatedBy(String createdBy) {
        return "<header xmlns='http://www.example.org/WS-HT'><initiator>"+createdBy+"</initiator></header>";
    }

    private HiseEndpoint getHiseEndpoint() {
        return (HiseEndpoint) super.getEndpoint();
    }
}
