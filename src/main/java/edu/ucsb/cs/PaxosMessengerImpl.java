package edu.ucsb.cs;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.functional.HeartbeatCallback;
import cocagne.paxos.functional.HeartbeatMessenger;
import edu.ucsb.cs.thrift.Ballot;
import edu.ucsb.cs.thrift.ThriftProposalID;
import edu.ucsb.cs.thrift.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private Log log = LogFactory.getLog(PaxosMessengerImpl.class);
    private ExecutorService workers = Executors.newCachedThreadPool();
    private edu.ucsb.cs.Executor executor;

    public PaxosMessengerImpl(String id, edu.ucsb.cs.Executor executor){
        this.nodeUID = id;
        conf = new MessengerConf();
        this.executor = executor;
    }

    public String getNodeUID(){ return nodeUID; }

    
    /* -----------------------------------------------
     *                   Essential
     * -----------------------------------------------
     */
    public void sendPrepare(final ProposalID proposalID){
        log.debug("sendPrepare: proposalID" + proposalID);
        // for address in map
        //      connection( address.recvPrepare(nodeUID, proposalID) )
        for (int i = 0; i < conf.getMessengerConfigurations().size(); i++) {

            doSendPrepare(proposalID, i);
        }
    }

    private void doSendPrepare(final ProposalID proposalID, final int i) {

        workers.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    TTransport transport;
                    Messenger oneMessenger = conf.getOneMessenger(i);
                    log.debug("sendPrepare: sending to " + oneMessenger.getAddress());
                    transport = new TSocket(oneMessenger.getAddress(), oneMessenger.getPort());
                    transport.open();

                    TProtocol protocol = new TBinaryProtocol(transport);
                    Ballot.Client client = new Ballot.Client(protocol);

                    client.prepare(nodeUID,new ThriftProposalID(proposalID.getNumber(),proposalID.getUID()));

                    transport.close();
                } catch (TException x) {
                    log.error("doSendPrepare: Error sending the prepare. See exception \n"+ x);
                    x.printStackTrace();
                }
            }
        });
    }

    public void sendPromise(String proposerUID, ProposalID proposalID, ProposalID previousID, Object acceptedValue){
        log.debug("sendPromise: proposerUID " + proposerUID + ", proposalID " + proposalID + ", previousID " + previousID + ", acceptedValue " + acceptedValue);
        // only send to proposerUID
        //      connection( proposerUID.recvPromise( nodeID, proposalID, previousID, acceptedValue)

        try {
            TTransport transport;
            Messenger m = conf.getOneMessenger(Integer.parseInt(proposerUID));
            log.debug("sendPromise: sending to " + m.getAddress());
            transport = new TSocket(m.getAddress(), m.getPort());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Ballot.Client client = new Ballot.Client(protocol);
            ThriftProposalID thriftPreviousID ;

            if(previousID == null){
                thriftPreviousID =  new ThriftProposalID(-1, "null");
            } else{
                thriftPreviousID  = new ThriftProposalID(previousID.getNumber(), previousID.getUID());
            }


            // If we have the very first request, and previousUID is null
            // send fake "lowest" previous proposal
            client.promise(nodeUID,
                    new ThriftProposalID(proposalID.getNumber(), proposalID.getUID()),
                    thriftPreviousID,
                    (Transaction) acceptedValue
            );
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
            log.error("sendPromise: Error sending the promise. See exception \n"+ x);
        }
        log.debug("sendPromise: Promise sent to " + proposerUID);
    }

    public void sendAccept(ProposalID proposalID, Object proposalValue){
        log.debug("sendAccept: proposalID" + proposalID + ", proposalValue " + proposalValue);
        // for address in list
        //      connection( address.recvAcceptRequest( nodeUID, proposalID, proposalValue)

        for (int i = 0; i < conf.getMessengerConfigurations().size(); i++) {
            doSendAccept(proposalID, (Transaction) proposalValue, i);
        }

    }

    private void doSendAccept(final ProposalID proposalID, final Transaction proposalValue, final int i) {
        workers.submit(new Runnable() {
            @Override
            public void run() {
                Messenger m = conf.getOneMessenger(i);

                try {
                    TTransport transport;
                    log.debug("sendAccept: sending to " + m.getAddress());
                    transport = new TSocket(m.getAddress(), m.getPort());
                    transport.open();

                    TProtocol protocol = new TBinaryProtocol(transport);
                    Ballot.Client client = new Ballot.Client(protocol);

                    client.accept(nodeUID, new ThriftProposalID(proposalID.getNumber(), proposalID.getUID()), proposalValue);

                    transport.close();
                } catch (TException x) {
                    x.printStackTrace();
                    log.error("doSendAccept: Error sending the accept. See exception \n"+ x);
                }
            }
        });

    }

    public void sendAccepted(ProposalID proposalID, Object acceptedValue){
        log.debug("sendAccepted: proposalID" + proposalID + ", acceptedValue " + acceptedValue);
        // send to leader? or just broadcast to everyone?
        //      connection( address.recvAccepted( nodeUID, proposalID, acceptedValue)

        for (int i = 0; i < conf.getMessengerConfigurations().size(); i++) {
            doSendAccepted(proposalID, (Transaction) acceptedValue, i);
        }

    }

    private void doSendAccepted(final ProposalID proposalID, final Transaction acceptedValue, final int i) {

        workers.submit(new Runnable() {
            @Override
            public void run() {
                Messenger m = conf.getOneMessenger(i);

                try {
                    TTransport transport;
                    log.debug("sendAccepted: sending to " + m.getAddress());
                    transport = new TSocket(m.getAddress(), m.getPort());
                    transport.open();

                    TProtocol protocol = new TBinaryProtocol(transport);
                    Ballot.Client client = new Ballot.Client(protocol);

                    client.accepted(nodeUID, new ThriftProposalID(proposalID.getNumber(), proposalID.getUID()), acceptedValue);

                    transport.close();
                } catch (TException x) {
                    x.printStackTrace();
                    log.error("doSendAccepted: Error sending the accepted. See exception \n"+ x);
                }
            }
        });
    }

    /**
     * Called when a node has received "accepted" messages from a majority
     * @param proposalID the proposal ID of the accepted proposal
     * @param value the value of the accepted proposal
     */
    public void onResolution(ProposalID proposalID, Object value){

        log.debug("onResolution: proposalID " + proposalID + ", acceptedValue " + value + " have been decided! Yay Paxos!");
        // for address in list
        //      write to file

        executor.enqueue((Transaction)value);
        log.info("Transaction completed");
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
        log.debug("sendPrepareNACK: proposerUID " + proposerUID + ", proposalID "+ proposalID + ", promisedID " + promisedID);

        try {
            TTransport transport;
            Messenger m = conf.getOneMessenger(Integer.parseInt(proposerUID));
            log.debug("sendPrepareNACK: sending to " + m.getAddress());
            transport = new TSocket(m.getAddress(), m.getPort());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Ballot.Client client = new Ballot.Client(protocol);

            client.prepareNACK(nodeUID,
                    new ThriftProposalID(proposalID.getNumber(), proposalID.getUID()),
                    new ThriftProposalID(promisedID.getNumber(), promisedID.getUID())
            );
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
            log.error("sendPrepareNACK: Error sending the prepareNACK. See exception \n"+ x);
        }

    }

    public void sendAcceptNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID){
        // only send to proposerUID
        //      connection( proposerUID.recvAcceptNACK(proposerUID, proposalID, promisedID)
        log.debug("sendAcceptNACK: proposerUID " + proposerUID + ", proposalID "+ proposalID + ", promisedID " + promisedID);

        try {
            TTransport transport;
            Messenger m = conf.getOneMessenger(Integer.parseInt(proposerUID));
            log.debug("sendAcceptNACK: sending to " + m.getAddress());
            transport = new TSocket(m.getAddress(), m.getPort());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Ballot.Client client = new Ballot.Client(protocol);

            client.acceptNACK(nodeUID,
                    new ThriftProposalID(proposalID.getNumber(), proposalID.getUID()),
                    new ThriftProposalID(promisedID.getNumber(), promisedID.getUID())
            );
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
            log.error("sendAcceptNACK: Error sending the acceptNACK. See exception \n"+ x);
        }

    }

    /**
     * Called when a node has received "promise" messages from a majority.
     * This is simply used to record the history of leadership.
     */
    public void onLeadershipAcquired(){
        // Add to log the acquisition of leadership by nodeUID
        log.debug("onLeadershipAcquired: " + nodeUID + " has become the leader");
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
        log.debug("onLeadershipLost: " + nodeUID + " lost leadership");
    }

    public void onLeadershipChange(String previousLeaderUID, String newLeaderUID){
        // record in log the change in leadership
        log.debug("onLeadershipChange: previousLeader " + previousLeaderUID + ", newLeader " + newLeaderUID);
    }

}
