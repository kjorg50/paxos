package edu.ucsb.cs.thrift;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.functional.HeartbeatNode;
import edu.ucsb.cs.PaxosHandler;
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
    /*
        ==============================================================
        Thrift methods' implementation
        ==============================================================
     */

    @Override
    public void prepare(String myId, ThriftProposalID propID) throws TException {
        log.debug("prepare: myId " + myId + ", propID " + propID.toString());
        heartbeatNode.receivePrepare(myId, new ProposalID((int) propID.getBallotNumber(), propID.getUid()));
    }

    @Override
    public void promise(String myId, ThriftProposalID propID, ThriftProposalID prevPropId, long acceptedValue) throws TException {
        log.debug("promise: myId " + myId + ", propID " + propID + ", prevPropId " + prevPropId + ", acceptedValue " + acceptedValue);

    }

    @Override
    public void accept(String myId, ThriftProposalID propID, long acceptedValue) throws TException {

    }

    @Override
    public void accepted(String myId, ThriftProposalID propID, long acceptedValue) throws TException {

    }

    @Override
    public void decide(ThriftProposalID propID, long value) throws TException {

    }

    @Override
    public void prepareNACK(String myId, ThriftProposalID propID, ThriftProposalID promisedID) throws TException {

    }

    @Override
    public void acceptNACK(String myId, ThriftProposalID propID, ThriftProposalID promisedID) throws TException {

    }

    @Override
    public void heartbeat(String myId, ThriftProposalID leaderPropID) throws TException {

    }

    @Override
    public List<Long> update(long lastAcceptedBallot) throws TException {
        return null;
    }
}
