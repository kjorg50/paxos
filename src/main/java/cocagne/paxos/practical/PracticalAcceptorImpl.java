package cocagne.paxos.practical;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

import cocagne.paxos.essential.EssentialAcceptorImpl;
import cocagne.paxos.essential.ProposalID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class PracticalAcceptorImpl extends EssentialAcceptorImpl implements PracticalAcceptor {
	
	protected String  pendingAccepted = null;
	protected String  pendingPromise  = null;
	protected boolean active          = true;
	private Log log = LogFactory.getLog(PracticalAcceptorImpl.class);
	
	public PracticalAcceptorImpl(PracticalMessenger messenger) {
		super(messenger);
	}


	// If there is some pendingAccepted or pendingPromise, then persistence is required
	@Override
	public boolean persistenceRequired() {
		return pendingAccepted != null || pendingPromise != null;
	}
	

	@Override
	public void recover(ProposalID promisedID, ProposalID acceptedID, Object acceptedValue) {
		this.promisedID    = promisedID;
		this.acceptedID    = acceptedID;
		this.acceptedValue = acceptedValue;
	}
	

	@Override
	public void receivePrepare(String fromUID, ProposalID proposalID) {
		log.debug("PracticalAcceptorImpl: receivePrepare fromUID " + fromUID + " proposalID " + proposalID.toString());
		if (this.promisedID != null && proposalID.equals(promisedID)) { // duplicate message, I already promised to this proposal
			if (active)
				messenger.sendPromise(fromUID, proposalID, acceptedID, acceptedValue);
		}
		else if (this.promisedID == null || proposalID.isGreaterThan(promisedID)) { // first proposal, or newer proposal
			if (pendingPromise == null) {											// promise to this Prepare
				promisedID = proposalID;
				if (active)
					pendingPromise = fromUID;
			}
		}
		else {
			// already promised to another, and/or proposal is old, send NACK
			if (active)
				((PracticalMessenger)messenger).sendPrepareNACK(fromUID, proposalID, promisedID);
		}
	}
	

	@Override
	public void receiveAcceptRequest(String fromUID, ProposalID proposalID,
			Object value) {
		// if this matches our already accepted proposal, send the "accepted" message again
		if (acceptedID != null && proposalID.equals(acceptedID) && acceptedValue.equals(value)) {
			if (active)
				messenger.sendAccepted(proposalID, value);
		}
		else if (promisedID == null || proposalID.isGreaterThan(promisedID) || proposalID.equals(promisedID)) {
			if (pendingAccepted == null) {  // if we have not made any promises, or have any pending accepted values
				 							// then we can accept this request
				promisedID    = proposalID;
				acceptedID    = proposalID;
				acceptedValue = value;
				
				if (active)
					pendingAccepted = fromUID;
			}
		}
		else { // already accepted to another, and/or proposal is old, send NACK
			if (active)
				((PracticalMessenger)messenger).sendAcceptNACK(fromUID, proposalID, promisedID);
		}
	}
	
	// send any pending values if active
	@Override
	public void persisted() {
		if (active) {
			if (pendingPromise != null)
				messenger.sendPromise(pendingPromise, promisedID, acceptedID, acceptedValue);
			if (pendingAccepted != null)
				messenger.sendAccepted(acceptedID, acceptedValue);
		}
		pendingPromise  = null;
		pendingAccepted = null;
	}


	@Override
	public boolean isActive() {
		return active;
	}


	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
}
