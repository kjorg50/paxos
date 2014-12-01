package cocagne.paxos.essential;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */


public interface EssentialAcceptor {
	public void receivePrepare(String fromUID, ProposalID proposalID);
	
	public void receiveAcceptRequest(String fromUID, ProposalID proposalID, Object value);
}
