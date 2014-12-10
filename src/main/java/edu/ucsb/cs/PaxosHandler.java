package edu.ucsb.cs;

import cocagne.paxos.functional.HeartbeatNode;
import edu.ucsb.cs.thrift.ThriftClient;
import edu.ucsb.cs.thrift.ThriftServer;

import java.util.Stack;

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
    private Stack<Transaction> transactions;
    private Double balance;

    public PaxosHandler(int num){
        nodeNum = num;
        nodeUID = "node"+nodeNum;
        messenger = new PaxosMessengerImpl(nodeUID);
        node = new HeartbeatNode(messenger,nodeUID,MAJORITY,null,1000,5000);
        transactions = new Stack<Transaction>();
        balance = 0.0;

        ThriftServer.startThriftServer();
    }

    public void deposit(double amount){
//        Transaction newTxn;
//        if(transactions.empty()) {
//            newTxn = new Transaction(0.0, amount);
//        } else {
//            Double prev = transactions.peek().getAmount();
//            newTxn = new Transaction(prev, prev+amount);
//        }
//        node.setProposal(newTxn);
//        node.prepare(); // run paxos
//
//        while(!node.isComplete()){
//            // timeout after certain amount?
//            if(node.isComplete())
//                break;
//        }
//        Transaction result = (Transaction)node.getFinalValue();
//
//        transactions.add(result);

        ThriftClient.callClient();
        // TODO - add logging

        return;
    }

    public void withdraw(double amount){
        Transaction newTxn;
        if(transactions.empty()) {
            newTxn = new Transaction(0.0, -amount);
        } else {
            Double prev = transactions.peek().getAmount();
            newTxn = new Transaction(prev, prev-amount);
        }
        node.setProposal(newTxn);
        node.prepare(); // run paxos

        while(!node.isComplete()){
            // timeout after certain amount?
            if(node.isComplete())
                break;
        }
        Transaction result = (Transaction)node.getFinalValue();

        transactions.add(result);

        // TODO - add logging

        return;
    }

    public double getBalance(){
        // TODO - add logging
        return transactions.peek().getAmount();
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
