package edu.ucsb.cs.thrift;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.functional.HeartbeatNode;
import edu.ucsb.cs.Executor;
import edu.ucsb.cs.Main;
import edu.ucsb.cs.PaxosHandler;
import edu.ucsb.cs.PaxosMessengerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;

import java.util.List;

/**
 * Created by kylejorgensen on 12/10/14.
 */
public class BallotHandler implements Ballot.Iface{
    public HeartbeatNode heartbeatNode;
    private Log log = LogFactory.getLog(BallotHandler.class);

    public BallotHandler(HeartbeatNode _heartbeatNode){
        heartbeatNode = _heartbeatNode;
    }

    public void setHeartbeatNode(HeartbeatNode hbn) {
        this.heartbeatNode = hbn;
    }
    /*
        ==============================================================
        Thrift methods' implementation
        ==============================================================
     */

    public static final int MAJORITY = 2;

    @Override
    public void prepare(String myId, ThriftProposalID propID) throws TException {
        log.debug("prepare: myId " + myId + ", propID " + propID);
        PaxosMessengerImpl messenger = new PaxosMessengerImpl(Main.nodeNumber, Executor.getInstance());
        if (!myId.equals(Main.nodeNumber)) {
            HeartbeatNode hbn = new HeartbeatNode(messenger, Main.nodeNumber,MAJORITY,null,1000,5000);
            this.heartbeatNode = hbn;
        }
        heartbeatNode.receivePrepare(myId, new ProposalID(propID.getBallotNumber(), propID.getUid()));
    }

    /**
     *
     * @param myId ID from promisee
     * @param propID
     * @param prevPropId
     * @param acceptedValue
     * @throws TException
     */
    @Override
    public void promise(String myId, ThriftProposalID propID, ThriftProposalID prevPropId, Transaction acceptedValue) throws TException {
        log.debug("promise: myId " + myId + ", propID " + propID + ", prevPropId " + prevPropId + ", acceptedValue " + acceptedValue);
        heartbeatNode.receivePromise(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                new ProposalID(prevPropId.getBallotNumber(), prevPropId.getUid()),
                acceptedValue);
    }

    @Override
    public void accept(String myId, ThriftProposalID propID, Transaction acceptedValue) throws TException {
        log.debug("accept: myId " + myId + ", propID " + propID + ", acceptedValue " + acceptedValue);
        heartbeatNode.receiveAcceptRequest(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                acceptedValue);
    }

    @Override
    public void accepted(String myId, ThriftProposalID propID, Transaction acceptedValue) throws TException {
        log.debug("accepted: myId " + myId + ", propID " + propID + ", acceptedValue " + acceptedValue);
        heartbeatNode.receiveAccepted(myId, new ProposalID(propID.getBallotNumber(), propID.getUid()), acceptedValue);

    }

    @Override
    public void prepareNACK(String myId, ThriftProposalID propID, ThriftProposalID promisedID) throws TException {
        log.debug("prepareNACK: myId " + myId + ", propID " + propID + ", promisedID " + promisedID);
        heartbeatNode.receivePrepareNACK(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                new ProposalID(promisedID.getBallotNumber(), promisedID.getUid()));
    }

    @Override
    public void acceptNACK(String myId, ThriftProposalID propID, ThriftProposalID promisedID) throws TException {
        log.debug("acceptNACK: myId " + myId + ", propID " + propID + ", promisedID " + promisedID);
        heartbeatNode.receiveAcceptNACK(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                new ProposalID(promisedID.getBallotNumber(), promisedID.getUid()));
    }

    @Override
    public void heartbeat(String myId, ThriftProposalID leaderPropID) throws TException {

    }

    @Override
    public List<Transaction> update(int lastAcceptedBallot) throws TException {
        return null;
    }
}
