package cocagne.paxos.practical;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

import cocagne.paxos.essential.EssentialMessenger;
import cocagne.paxos.essential.ProposalID;

public interface PracticalMessenger extends EssentialMessenger {
	
	public void sendPrepareNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID);
	
	public void sendAcceptNACK(String proposerUID, ProposalID proposalID, ProposalID promisedID);
	
	public void onLeadershipAcquired();
}
