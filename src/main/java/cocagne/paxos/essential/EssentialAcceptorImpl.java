package cocagne.paxos.essential;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

public class EssentialAcceptorImpl implements EssentialAcceptor {
	
	protected EssentialMessenger messenger;
	protected ProposalID         promisedID;
	protected ProposalID         acceptedID;
	protected Object             acceptedValue;

	public EssentialAcceptorImpl( EssentialMessenger messenger ) {
		this.messenger = messenger;
	}

	@Override
	public void receivePrepare(String fromUID, ProposalID proposalID) {
		
		if (this.promisedID != null && proposalID.equals(promisedID)) { // duplicate message, I already promised to this proposal
			messenger.sendPromise(fromUID, proposalID, acceptedID, acceptedValue);
		}
		else if (this.promisedID == null || proposalID.isGreaterThan(promisedID)) { // first proposal, or newer proposal
																					// promise to this Prepare
			promisedID = proposalID;
			messenger.sendPromise(fromUID, proposalID, acceptedID, acceptedValue);
		}
	}

	@Override
	public void receiveAcceptRequest(String fromUID, ProposalID proposalID,
			Object value) {
		if (promisedID == null || proposalID.isGreaterThan(promisedID) || proposalID.equals(promisedID)) {
			promisedID    = proposalID;
			acceptedID    = proposalID;
			acceptedValue = value;
			
			messenger.sendAccepted(acceptedID, acceptedValue);
		}
	}

	public EssentialMessenger getMessenger() {
		return messenger;
	}

	public ProposalID getPromisedID() {
		return promisedID;
	}

	public ProposalID getAcceptedID() {
		return acceptedID;
	}

	public Object getAcceptedValue() {
		return acceptedValue;
	}

}
