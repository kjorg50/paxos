package cocagne.paxos.essential;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */


public interface EssentialProposer {
	
	public void setProposal(Object value);
	
	public void prepare();
	
	public void receivePromise(String fromUID, ProposalID proposalID, ProposalID prevAcceptedID, Object prevAcceptedValue);

}
