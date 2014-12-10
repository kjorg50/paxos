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
import java.util.HashMap;
import java.util.List;

public class BallotHandler implements Ballot.Iface {
    @Override
    public void prepare(long ballotNumber, long myId) throws TException {

    }

    @Override
    public void ack(long ballotNumber, long acceptedNumber, long acceptedVal) throws TException {

    }

    @Override
    public void accept(long ballotNumber, long leaderVal) throws TException {

    }

    @Override
    public void accepted(long ballotNumber, long val) throws TException {

    }

    @Override
    public void decide(long ballotNumber, long value) throws TException {

    }

    @Override
    public List<Long> update(long lastAcceptedBallot) throws TException {
        return null;
    }

    @Override
    public boolean isLeader() throws TException {
        System.out.println("Is leader here");
        return false;
    }

    // private HashMap<Integer,SharedStruct> log;


//    public int add(int n1, int n2) {
//        System.out.println("add(" + n1 + "," + n2 + ")");
//        return n1 + n2;
//    }





}

