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
        import org.apache.thrift.TException;
        import org.apache.thrift.transport.TSSLTransportFactory;
        import org.apache.thrift.transport.TTransport;
        import org.apache.thrift.transport.TSocket;
        import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
        import org.apache.thrift.protocol.TBinaryProtocol;
        import org.apache.thrift.protocol.TProtocol;

public class ThriftClient {


    public static void callClient() {



        try {
            TTransport transport;
            transport = new TSocket("localhost", 9090);
            transport.open();


            TProtocol protocol = new  TBinaryProtocol(transport);
            Ballot.Client client = new Ballot.Client(protocol);

            // perform(client);

            client.isLeader();
            System.out.println("isLeader()" +  client.isLeader());


            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    public static void main(String [] args) {



        try {
            TTransport transport;
                transport = new TSocket("localhost", 9090);
                transport.open();


            TProtocol protocol = new  TBinaryProtocol(transport);
            Ballot.Client client = new Ballot.Client(protocol);

           // perform(client);

            //client.isLeader();
            client.decide(1L,2L);
            System.out.println("decide() called");


            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(Ballot.Client client) throws TException
    {
//        client.ping();
//        System.out.println("ping()");
//
//        int sum = client.add(1,1);
//        System.out.println("1+1=" + sum);
//
        client.isLeader();
        System.out.println("isLeader()" +  client.isLeader());



        //SharedStruct log = client.getStruct(1);

    }
}
