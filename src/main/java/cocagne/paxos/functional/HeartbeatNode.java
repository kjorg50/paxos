package cocagne.paxos.functional;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

import java.util.HashSet;

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.practical.PracticalNode;

public class HeartbeatNode extends PracticalNode {
	
	protected HeartbeatMessenger messenger;
	protected String             leaderUID;
	protected ProposalID         leaderProposalID;
	protected long               lastHeartbeatTimestamp;
	protected long               lastPrepareTimestamp;
	protected long               heartbeatPeriod         = 1000; // Milliseconds
	protected long               livenessWindow          = 5000; // Milliseconds
	protected boolean            acquiringLeadership     = false;
	protected HashSet<String>    acceptNACKs             = new HashSet<String>();
	
	
	public HeartbeatNode(HeartbeatMessenger messenger, String proposerUID,
			int quorumSize, String leaderUID, int heartbeatPeriod, int livenessWindow) {
		super(messenger, proposerUID, quorumSize);
		
		this.messenger       = messenger;
		this.leaderUID       = leaderUID;
		this.heartbeatPeriod = heartbeatPeriod;
		this.livenessWindow  = livenessWindow;
		
		leaderProposalID       = null;
		lastHeartbeatTimestamp = timestamp();
		lastPrepareTimestamp   = timestamp();
		
		if (leaderUID != null && proposerUID.equals(leaderUID))
			setLeader(true);
	}
	
	public long timestamp() {
		return System.currentTimeMillis();
	}
	
	public String getLeaderUID() {
		return leaderUID;
	}
	
	public ProposalID getLeaderProposalID() {
		return leaderProposalID;
	}
	
	public void setLeaderProposalID( ProposalID newLeaderID ) {
		leaderProposalID = newLeaderID;
	}
	
	public boolean isAcquiringLeadership() {
		return acquiringLeadership;
	}

	@Override
	public void prepare(boolean incrementProposalNumber) {
		if (incrementProposalNumber) // if we want to start a new election, clear out old NACKs
			acceptNACKs.clear();
		super.prepare(incrementProposalNumber);
	}
	
	public boolean leaderIsAlive() {
		return timestamp() - lastHeartbeatTimestamp <= livenessWindow;
	}
	
	public boolean observedRecentPrepare() {
		return timestamp() - lastPrepareTimestamp <= livenessWindow * 1.5;
	}
	
	// If the system is not "live", and there is no leader, then aquire leadership
	public void pollLiveness() {
		if (!leaderIsAlive() && !observedRecentPrepare()) {
			if (acquiringLeadership)
				prepare();
			else
				acquireLeadership();
		}
	}
	
	public void receiveHeartbeat(String fromUID, ProposalID proposalID) {
		
		// If there is no leader yet, or if there is a new proposal
		if (leaderProposalID == null || proposalID.isGreaterThan(leaderProposalID)) {
			acquiringLeadership = false;
			String oldLeaderUID = leaderUID;
			
			leaderUID        = fromUID;
			leaderProposalID = proposalID;
			
			// If I am leader, and this heartbeat is from a new proposer,
			// I lose leadership and become a witness
			if (isLeader() && !fromUID.equals(getProposerUID())) {
				setLeader(false);
				messenger.onLeadershipLost();
				observeProposal(fromUID, proposalID);
			}
			
			messenger.onLeadershipChange(oldLeaderUID, fromUID);
		}
		
		// If heartbeat is from leader, update timestamp
		if (leaderProposalID != null && leaderProposalID.equals(proposalID))
			lastHeartbeatTimestamp = timestamp();
	}
	
	public void pulse() {
		if (isLeader()) {
			receiveHeartbeat(getProposerUID(), getProposalID());
			messenger.sendHeartbeat(getProposalID());
			messenger.schedule(heartbeatPeriod, new HeartbeatCallback () { 
				public void execute() { pulse(); }
			});
		}
	}

	public void acquireLeadership() {
		if (leaderIsAlive())
			acquiringLeadership = false;
		else {
			acquiringLeadership = true;
			prepare();
		}
	}
	
	@Override
	public void receivePrepare(String fromUID, ProposalID proposalID) {
		super.receivePrepare(fromUID, proposalID);

		// If the prepare came from another node, update the timestamp
		if (!proposalID.equals(getProposalID()))
			lastPrepareTimestamp = timestamp();
	}
	
	@Override
	public void receivePromise(String fromUID, ProposalID proposalID,
			ProposalID prevAcceptedID, Object prevAcceptedValue) {
		String preLeaderUID = leaderUID;
		
		super.receivePromise(fromUID, proposalID, prevAcceptedID, prevAcceptedValue);

		// if I now am the leader and there was no previous leader
		if (preLeaderUID == null && isLeader()) {
			String oldLeaderUID = getProposerUID();
			
			leaderUID           = getProposerUID();
			leaderProposalID    = getProposalID();
			acquiringLeadership = false;
			
			pulse();
			
			messenger.onLeadershipChange(oldLeaderUID, leaderUID);
		}
	}
	
	@Override
	public void receivePrepareNACK(String proposerUID, ProposalID proposalID,
			ProposalID promisedID) {
		super.receivePrepareNACK(proposerUID, proposalID, promisedID);
		
		if (acquiringLeadership)
			prepare();
	}
	
	@Override
	public void receiveAcceptNACK(String proposerUID, ProposalID proposalID,
			ProposalID promisedID) {
		super.receiveAcceptNACK(proposerUID, proposalID, promisedID);
		
		// if our proposal was NACKed, add the UID to the list
		if (proposalID.equals(getProposalID()))
			acceptNACKs.add(proposerUID);
		
		// If we are the leader and have been NACKed by a majority, lose leadership
		if (isLeader() && acceptNACKs.size() >= getQuorumSize()) {
			setLeader(false);
			leaderUID        = null;
			leaderProposalID = null;
			messenger.onLeadershipLost();
			messenger.onLeadershipChange(getProposerUID(), null);
			observeProposal(proposerUID, proposalID);
		}
	}
}
