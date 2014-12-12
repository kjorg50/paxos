package edu.ucsb.cs.thrift;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.functional.HeartbeatNode;
import edu.ucsb.cs.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;

import java.util.List;

/**
 * Created by kylejorgensen on 12/10/14.
 */
public class BallotHandler implements Ballot.Iface{

    private Log log = LogFactory.getLog(BallotHandler.class);

    /*
        ==============================================================
        Thrift methods' implementation
        ==============================================================
     */

    @Override
    public void prepare(String myId, ThriftProposalID propID, String txnId) throws TException {
        log.debug("prepare: myId " + myId + ", propID " + propID);
        HeartbeatNode heartbeatNode = HBNStore.getInstance().getNode(txnId, Main.nodeNumber);
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
    public void promise(String myId, ThriftProposalID propID, ThriftProposalID prevPropId,
                        Transaction acceptedValue, String txnId) throws TException {
        log.debug("promise: myId " + myId + ", propID " + propID + ", prevPropId " + prevPropId + ", acceptedValue " + acceptedValue);
        HeartbeatNode heartbeatNode = HBNStore.getInstance().getNode(txnId, Main.nodeNumber);
        heartbeatNode.receivePromise(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                new ProposalID(prevPropId.getBallotNumber(), prevPropId.getUid()),
                acceptedValue);
    }

    @Override
    public void accept(String myId, ThriftProposalID propID, Transaction acceptedValue, String txnId) throws TException {
        log.debug("accept: myId " + myId + ", propID " + propID + ", acceptedValue " + acceptedValue);
        HeartbeatNode heartbeatNode = HBNStore.getInstance().getNode(txnId, Main.nodeNumber);
        heartbeatNode.receiveAcceptRequest(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                acceptedValue);
    }

    @Override
    public void accepted(String myId, ThriftProposalID propID,
                         Transaction acceptedValue, String txnId) throws TException {
        log.debug("accepted: myId " + myId + ", propID " + propID + ", acceptedValue " + acceptedValue);
        HeartbeatNode heartbeatNode = HBNStore.getInstance().getNode(txnId, Main.nodeNumber);
        heartbeatNode.receiveAccepted(myId, new ProposalID(propID.getBallotNumber(), propID.getUid()), acceptedValue);

    }

    @Override
    public void prepareNACK(String myId, ThriftProposalID propID,
                            ThriftProposalID promisedID, String txnId) throws TException {
        log.debug("prepareNACK: myId " + myId + ", propID " + propID + ", promisedID " + promisedID);
        HeartbeatNode heartbeatNode = HBNStore.getInstance().getNode(txnId, Main.nodeNumber);
        heartbeatNode.receivePrepareNACK(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                new ProposalID(promisedID.getBallotNumber(), promisedID.getUid()));
    }

    @Override
    public void acceptNACK(String myId, ThriftProposalID propID,
                           ThriftProposalID promisedID, String txnId) throws TException {
        log.debug("acceptNACK: myId " + myId + ", propID " + propID + ", promisedID " + promisedID);
        HeartbeatNode heartbeatNode = HBNStore.getInstance().getNode(txnId, Main.nodeNumber);
        heartbeatNode.receiveAcceptNACK(myId,
                new ProposalID(propID.getBallotNumber(), propID.getUid()),
                new ProposalID(promisedID.getBallotNumber(), promisedID.getUid()));
    }

    @Override
    public void heartbeat(String myId, ThriftProposalID leaderPropID) throws TException {

    }

    @Override
    public List<Transaction> update(int lastAcceptedBallot) throws TException {
        return Executor.getInstance().sendRecovery(lastAcceptedBallot);
    }
}
