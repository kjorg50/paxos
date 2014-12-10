package edu.ucsb.cs;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.functional.HeartbeatCallback;
import cocagne.paxos.functional.HeartbeatMessenger;
import edu.ucsb.cs.thrift.Ballot;
import edu.ucsb.cs.thrift.ThriftProposalID;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * This class will handle the messaging between different nodes in the system
 */
public class PaxosMessengerImpl implements HeartbeatMessenger {

    private String nodeUID;
    private MessengerConf conf;

    public PaxosMessengerImpl(String id){
        this.nodeUID = id;
        conf = new MessengerConf();
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
        System.out.println("PaxosMessengerImpl: sendPrepare" + proposalID.toString());
        // for address in map
        //      connection( address.recvPrepare(nodeUID, proposalID) )
        for (int i = 0; i < conf.getMessengerConfigurations().size(); i++) {
            Messenger m = conf.getOneMessenger(i);

            try {
                TTransport transport;
                System.out.println("*** "+m.getAddress());
                transport = new TSocket(m.getAddress(), m.getPort());
                transport.open();

                System.out.println("*** Before thrift client creation");
                TProtocol protocol = new TBinaryProtocol(transport);
                Ballot.Client client = new Ballot.Client(protocol);

                System.out.println("*** After thrift client creation, before prepare sent");
                client.prepare(nodeUID,new ThriftProposalID(proposalID.getNumber(),proposalID.getUID()));

                System.out.println("*** After prepare sent by Thrift ");
                transport.close();
            } catch (TException x) {
                x.printStackTrace();
            }
        }

    }

    public void sendPromise(String proposerUID, ProposalID proposalID, ProposalID previousID, Object acceptedValue){
        System.out.println("PaxosMessengerImpl: sendPromise " + " proposerUID " +  proposerUID + "proposalID" +  proposalID + "previousID" + previousID + " acceptedValue " + acceptedValue);
        // only send to proposerUID
        //      connection( proposerUID.recvPromise( nodeID, proposalID, previousID, acceptedValue)

        try {
            TTransport transport;
            // proposerUID = 0
            Messenger m = conf.getOneMessenger(0);
            System.out.println("PaxosMessengerImpl sendPromise sending to: " + m.getAddress());
            transport = new TSocket(m.getAddress(), m.getPort());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Ballot.Client client = new Ballot.Client(protocol);

            client.promise(nodeUID,
                    new ThriftProposalID(proposalID.getNumber(), proposalID.getUID()),
                    new ThriftProposalID(previousID.getNumber(), previousID.getUID()),
                    (Long)acceptedValue
            );

// TODO FIX THIS Object - AcceptedValue

//        public void promise(String myId, ThriftProposalID propID, ThriftProposalID prevPropId, long acceptedValue) throws TException {


            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }


        System.out.println(proposerUID + " has sent a promise (paxos send promise)");
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
        // for address in list
        //      write to file

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

}
