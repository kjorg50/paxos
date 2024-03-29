package cocagne.paxos.essential;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

import java.util.HashSet;


public class EssentialProposerImpl implements EssentialProposer {
	
	protected EssentialMessenger  messenger;
    protected String              proposerUID;
    protected final int           quorumSize;

    protected ProposalID          proposalID;
    protected Object              proposedValue      = null;
    protected ProposalID          lastAcceptedID     = null;
    protected HashSet<String>     promisesReceived   = new HashSet<String>();
    
    public EssentialProposerImpl(EssentialMessenger messenger, String proposerUID, int quorumSize) {
		this.messenger   = messenger;
		this.proposerUID = proposerUID;
		this.quorumSize  = quorumSize;
		this.proposalID  = new ProposalID(0, proposerUID);
	}

	@Override
	public void setProposal(Object value) {
		if ( proposedValue == null )
			proposedValue = value;
	}

	@Override
	public void prepare() {
		promisesReceived.clear();
		
		proposalID.incrementNumber();
		
		messenger.sendPrepare(proposalID);
	}

	@Override
	public void receivePromise(String fromUID, ProposalID proposalID,
			ProposalID prevAcceptedID, Object prevAcceptedValue) {

		// if the responder is not on this election, or has already been received, return
		if ( !proposalID.equals(this.proposalID) || promisesReceived.contains(fromUID) ) 
			return;
		
		// otherwise, add it to the set
        promisesReceived.add( fromUID );

        // If there is a new proposal since the previously accepted one,
        //  then update the local values
        if (lastAcceptedID == null || prevAcceptedID.isGreaterThan(lastAcceptedID))
        {
        	lastAcceptedID = prevAcceptedID;

        	if (prevAcceptedValue != null)
        		proposedValue = prevAcceptedValue;
        }
        
        // If we have a majority, begin the accept phase
        if (promisesReceived.size() == quorumSize)
        	if (proposedValue != null)
        		messenger.sendAccept(this.proposalID, proposedValue);
	}

	public EssentialMessenger getMessenger() {
		return messenger;
	}

	public String getProposerUID() {
		return proposerUID;
	}

	public int getQuorumSize() {
		return quorumSize;
	}

	public ProposalID getProposalID() {
		return proposalID;
	}

	public Object getProposedValue() {
		return proposedValue;
	}

	public ProposalID getLastAcceptedID() {
		return lastAcceptedID;
	}
	
	public int numPromises() {
		return promisesReceived.size();
	}
}
