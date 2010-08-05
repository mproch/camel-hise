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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultExchange;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: mproch
 * Date: Mar 12, 2010
 * Time: 9:19:06 PM
 */
public class HiseConsumer extends DefaultConsumer {

    public HiseConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    Node consume(Node content, Node epr) {
            DefaultExchange ex = new DefaultExchange(getEndpoint().getCamelContext());
            ex.getIn().setBody(content);
            try {

                getProcessor().process(ex);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (ex.isFailed()) {
                handleFailedExchange(ex);
            }

            return ex.getOut().getBody(Node.class);

    }

    private void handleFailedExchange(Exchange ex) {
        Exception exception = ex.getException();
        if (ex != null) {
            throw exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
        }
        throw new RuntimeException(ex.getOut().getBody(String.class));
    }



}
