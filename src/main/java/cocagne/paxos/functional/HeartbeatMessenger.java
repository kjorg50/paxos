package cocagne.paxos.functional;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

import cocagne.paxos.essential.ProposalID;
import cocagne.paxos.practical.PracticalMessenger;

public interface HeartbeatMessenger extends PracticalMessenger {
	
	public void sendHeartbeat( ProposalID leaderProposalID);
	
	public void schedule(long millisecondDelay, HeartbeatCallback callback);
	
	public void onLeadershipLost();
	
	public void onLeadershipChange(String previousLeaderUID, String newLeaderUID);
}
