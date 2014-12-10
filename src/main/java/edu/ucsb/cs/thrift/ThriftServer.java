package edu.ucsb.cs.thrift;

/**
 * Created by nevena on 12/8/14.
 */


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import edu.ucsb.cs.PaxosMessengerImpl;
import org.apache.thrift.server.TServer;
        import org.apache.thrift.server.TServer.Args;
        import org.apache.thrift.server.TSimpleServer;
        import org.apache.thrift.server.TThreadPoolServer;
        import org.apache.thrift.transport.TSSLTransportFactory;
        import org.apache.thrift.transport.TServerSocket;
        import org.apache.thrift.transport.TServerTransport;
        import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TTransportException;

public class ThriftServer {

    public static PaxosMessengerImpl handler;

    public static Ballot.Processor processor;

    public static void startThriftServer(String nodeUID) {
        try {
            handler = new PaxosMessengerImpl(nodeUID);
            processor = new Ballot.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {

                    TServerTransport serverTransport = null;
                    try {
                        serverTransport = new TServerSocket(9090);
                    } catch (TTransportException e) {
                        e.printStackTrace();
                    }
                    TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

                    // Use this for a multithreaded server
                    // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

                    System.out.println("Starting the Thrift server...");
                    server.serve();

                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

}
