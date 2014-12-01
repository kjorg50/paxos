package edu.ucsb.cs;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.functional.HeartbeatCallback;
import cocagne.paxos.functional.HeartbeatMessenger;

/**
 * This class will handle the messaging between different nodes in the system
 */
public class PaxosMessengerImpl implements HeartbeatMessenger{

    // TODO - read from file to determine what nodes to talk to
    // maybe use an ArrayList to store them?
    
    /* -----------------------------------------------
     *                   Essential
     * -----------------------------------------------
     */
    public void sendPrepare(ProposalID proposalID){

    }

    public void sendPromise(String proposerUID, ProposalID proposalID, ProposalID previousID, Object acceptedValue){

    }

    public void sendAccept(ProposalID proposalID, Object proposalValue){

    }

    public void sendAccepted(ProposalID proposalID, Object acceptedValue){

    }

    public void onResolution(ProposalID proposalID, Object value){

    }

    /* -----------------------------------------------
     *                   Practical
     * -----------------------------------------------
     */
    public void sendPrepareNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID){

    }

    public void sendAcceptNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID){

    }

    public void onLeadershipAcquired(){

    }

    /* -----------------------------------------------
     *                   Heartbeat
     * -----------------------------------------------
     */
    public void sendHeartbeat( ProposalID leaderProposalID){

    }

    public void schedule(long millisecondDelay, HeartbeatCallback callback){

    }

    public void onLeadershipLost(){

    }

    public void onLeadershipChange(String previousLeaderUID, String newLeaderUID){

    }
}
