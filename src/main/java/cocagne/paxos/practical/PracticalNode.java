package cocagne.paxos.practical;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

import cocagne.paxos.essential.EssentialLearner;
import cocagne.paxos.essential.EssentialLearnerImpl;
import cocagne.paxos.essential.ProposalID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PracticalNode implements PracticalProposer, PracticalAcceptor, EssentialLearner {

	protected PracticalProposerImpl proposer;
	protected PracticalAcceptorImpl acceptor;
	protected EssentialLearnerImpl  learner;
	private Log log = LogFactory.getLog(PracticalNode.class);
	
	public PracticalNode(PracticalMessenger messenger, String proposerUID,
			int quorumSize) {
		
		proposer = new PracticalProposerImpl(messenger, proposerUID, quorumSize);
		acceptor = new PracticalAcceptorImpl(messenger);
		learner  = new EssentialLearnerImpl(messenger, quorumSize);
	}
	
	public boolean isActive() {
		return proposer.isActive();
	}

	public void setActive(boolean active) {
		proposer.setActive(active);
		acceptor.setActive(active);
	}

	//-------------------------------------------------------------------------
	// Learner
	//
	@Override
	public boolean isComplete() {
		return learner.isComplete();
	}

	@Override
	public void receiveAccepted(String fromUID, ProposalID proposalID,
			Object acceptedValue) {
		log.debug("receiveAccepted: fromUID " + fromUID + ", proposalID " + proposalID + ", acceptedValue " + acceptedValue);
		learner.receiveAccepted(fromUID, proposalID, acceptedValue);

	}
	
	@Override
	public Object getFinalValue() {
		return learner.getFinalValue();
	}

	@Override
	public ProposalID getFinalProposalID() {
		return learner.getFinalProposalID();
	}

	//-------------------------------------------------------------------------
	// Acceptor
	//
	@Override
	public void receivePrepare(String fromUID, ProposalID proposalID) {
		acceptor.receivePrepare(fromUID, proposalID);
	}

	@Override
	public void receiveAcceptRequest(String fromUID, ProposalID proposalID,
			Object value) {
		log.debug("receiveAcceptRequest: fromUID " + fromUID + ", proposalID " + proposalID + ", value " + value);
		acceptor.receiveAcceptRequest(fromUID, proposalID, value);
		if (acceptor.persistenceRequired()){
			acceptor.persisted();
		}
	}
	
	public ProposalID getPromisedID() {
		return acceptor.getPromisedID();
	}

	public ProposalID getAcceptedID() {
		return acceptor.getAcceptedID();
	}

	public Object getAcceptedValue() {
		return acceptor.getAcceptedValue();
	}
	
	public boolean persistenceRequired() {
		return acceptor.persistenceRequired();
	}
	
	public void recover(ProposalID promisedID, ProposalID acceptedID, Object acceptedValue) {
		acceptor.recover(promisedID, acceptedID, acceptedValue);
	}
	
	public void persisted() {
		acceptor.persisted();
	}

	//-------------------------------------------------------------------------
	// Proposer
	//
	@Override
	public void setProposal(Object value) {
		proposer.setProposal(value);
	}

	@Override
	public void prepare() {
		proposer.prepare();
	}
	
	public void prepare( boolean incrementProposalNumber ) {
		proposer.prepare(incrementProposalNumber);
	}

	@Override
	public void receivePromise(String fromUID, ProposalID proposalID,
			ProposalID prevAcceptedID, Object prevAcceptedValue) {
		log.debug("receivePromise: fromUID " + fromUID + ", proposal " + proposalID +
				", prevAcceptedID " +prevAcceptedID + ", prevAcceptedValue " + prevAcceptedValue);
		proposer.receivePromise(fromUID, proposalID, prevAcceptedID, prevAcceptedValue);
//		if (acceptor.persistenceRequired()){
//			acceptor.persisted();
//		}
	}
	
	public PracticalMessenger getMessenger() {
		return proposer.getMessenger();
	}

	public String getProposerUID() {
		return proposer.getProposerUID();
	}

	public int getQuorumSize() {
		return proposer.getQuorumSize();
	}

	public ProposalID getProposalID() {
		return proposer.getProposalID();
	}

	public Object getProposedValue() {
		return proposer.getProposedValue();
	}

	public ProposalID getLastAcceptedID() {
		return proposer.getLastAcceptedID();
	}
	
	public int numPromises() {
		return proposer.numPromises();
	}
	
	public void observeProposal(String fromUID, ProposalID proposalID) {
		proposer.observeProposal(fromUID, proposalID);
	}
	
	public void receivePrepareNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID) {
		proposer.receivePrepareNACK(proposerUID, proposalID, promisedID);
	}
	
	public void receiveAcceptNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID) {
		proposer.receiveAcceptNACK(proposerUID, proposalID, promisedID);
	}
	
	public void resendAccept() {
		proposer.resendAccept();
	}
	
	public boolean isLeader() {
		return proposer.isLeader();
	}

	public void setLeader(boolean leader) {
		proposer.setLeader(leader);
	}
}
