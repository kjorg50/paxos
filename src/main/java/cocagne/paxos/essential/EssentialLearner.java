package cocagne.paxos.essential;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */


public interface EssentialLearner {

	public boolean isComplete();
	
	public void receiveAccepted(String fromUID, ProposalID proposalID, Object acceptedValue);
	
	public Object getFinalValue();

	ProposalID getFinalProposalID();
}
