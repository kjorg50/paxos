package edu.ucsb.cs;

import java.io.*;
import java.net.*;
import java.util.List;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.functional.HeartbeatCallback;
import cocagne.paxos.functional.HeartbeatMessenger;
import edu.ucsb.cs.thrift.Ballot;
import org.apache.thrift.TException;

/**
 * This class will handle the messaging between different nodes in the system
 */
public class PaxosMessengerImpl implements HeartbeatMessenger, Ballot.Iface{

    public static final String SERVER_URL = "127.0.0.1";
    public static final int PORT = 5050;

    private String nodeUID;

    public PaxosMessengerImpl(String id){
        this.nodeUID = id;
    }

    public String getNodeUID(){ return nodeUID;
    }
    // TODO - read from file to determine what nodes to talk to?
    // maybe use an ArrayList to store them?
    
    /* -----------------------------------------------
     *                   Essential
     * -----------------------------------------------
     */
    public void sendPrepare(ProposalID proposalID){
        // for address in list
        //      connection( address.recvPrepare(nodeUID, proposalID) )

    }

    public void sendPromise(String proposerUID, ProposalID proposalID, ProposalID previousID, Object acceptedValue){
        // only send to proposerUID
        //      connection( proposerUID.recvPromise( nodeID, proposalID, previousID, acceptedValue)
    }

    public void sendAccept(ProposalID proposalID, Object proposalValue){
        // for address in list
        //      connection( address.recvAcceptRequest( nodeUID, proposalID, proposalValue)
    }

    public void sendAccepted(ProposalID proposalID, Object acceptedValue){
        // send to leader? or just broadcast to everyone?
        //      connection( address.recvAccepted( nodeUID, proposalID, acceptedValue)
    }

    /**
     * Called when a node has received "accepted" messages from a majority
     * @param proposalID the proposal ID of the accepted proposal
     * @param value the value of the accepted proposal
     */
    public void onResolution(ProposalID proposalID, Object value){
        // Add to log that this round of paxos has been resolved
        // "proposal number:" proposalID ", value:" value
    }

    /* -----------------------------------------------
     *                   Practical
     * -----------------------------------------------
     */
    public void sendPrepareNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID){
        // only send to proposerUID
        //      connection( proposerUID.recvPrepareNACK(proposerUID, proposalID, promisedID)
    }

    public void sendAcceptNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID){
        // only send to proposerUID
        //      connection( proposerUID.recvAcceptNACK(proposerUID, proposalID, promisedID)
    }

    /**
     * Called when a node has received "promise" messages from a majority.
     * This is simply used to record the history of leadership.
     */
    public void onLeadershipAcquired(){
        // Add to log the acquisition of leadership by nodeUID
    }

    /* -----------------------------------------------
     *                   Heartbeat
     * -----------------------------------------------
     */
    /**
     * Called from the pulse() function. Heartbeats are only send by the current leader
     * @param leaderProposalID the current proposal by the leader
     */
    public void sendHeartbeat( ProposalID leaderProposalID){
        // for address in list
        //      connection( address.recvHeartbeat( nodeUID, proposalID)
    }

    public void schedule(long millisecondDelay, HeartbeatCallback callback){
        // call the callback function every millisecondDelay milliseconds
        //      callback.execute()
    }

    public void onLeadershipLost(){
        // record in log the loss of leadership by nodeUID
    }

    public void onLeadershipChange(String previousLeaderUID, String newLeaderUID){
        // record in log the change in leadership
    }

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
        System.out.println("In decide(): ballot " + ballotNumber + " value " + value);
    }

    @Override
    public List<Long> update(long lastAcceptedBallot) throws TException {
        return null;
    }

    @Override
    public boolean isLeader() throws TException {
        return false;
    }
}
