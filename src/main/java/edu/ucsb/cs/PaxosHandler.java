package edu.ucsb.cs;

import cocagne.paxos.functional.HeartbeatNode;

/**
 * Created by kylejorgensen on 11/30/14.
 *
 * This class will handle the communication between the command line interface
 * and the actual Paxos implementation.
 */
public class PaxosHandler {
    public static final int MAJORITY=2; // quorum size

    private int nodeNum;
    private String nodeUID;
    private HeartbeatNode node;
    private PaxosMessengerImpl messenger;

    public PaxosHandler(int num){
        nodeNum = num;
        nodeUID = "node"+nodeNum;
        messenger = new PaxosMessengerImpl();
        node = new HeartbeatNode(messenger,nodeUID,MAJORITY,null,1000,5000);
    }

    public void deposit(double amount){
        // TODO - run paxos

        // TODO - add logging
    }

    public void withdraw(double amount){
        // TODO - run paxos

        // TODO - add logging
    }

    public double getBalance(){
        return 42.0; // STUB
        // TODO - add logging
    }

    public void fail(){
        node.setActive(false);
        // TODO - add logging
    }

    public void unfail(){
        node.setActive(true);
        // TODO - add logging
    }

    public int getNodeNum(){
        return nodeNum;
    }

    public String getNodeUID(){
        return nodeUID;
    }
}
