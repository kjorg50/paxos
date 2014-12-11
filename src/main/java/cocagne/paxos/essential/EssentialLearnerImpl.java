package cocagne.paxos.essential;
/**
 * Based on the code from https://github.com/cocagne/paxos
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;


public class EssentialLearnerImpl implements EssentialLearner {
	private Log log = LogFactory.getLog(EssentialLearnerImpl.class);
	class Proposal {
		int    acceptCount;
		int    retentionCount;
		Object value;
		
		Proposal(int acceptCount, int retentionCount, Object value) {
			this.acceptCount    = acceptCount;
			this.retentionCount = retentionCount;
			this.value          = value;
		}
	}
	
	private final EssentialMessenger      messenger;
	private final int                     quorumSize;
	private HashMap<ProposalID, Proposal> proposals       = new HashMap<ProposalID, Proposal>();
	private HashMap<String,  ProposalID>  acceptors       = new HashMap<String, ProposalID>();
	private Object                        finalValue      = null;
	private ProposalID                    finalProposalID = null;
	
	public EssentialLearnerImpl( EssentialMessenger messenger, int quorumSize ) {
		this.messenger  = messenger;
		this.quorumSize = quorumSize;
	}

	@Override
	public boolean isComplete() {
		return finalValue != null;
	}

	@Override
	public void receiveAccepted(String fromUID, ProposalID proposalID,
			Object acceptedValue) {
		log.debug("receiveAccepted: fromUID " + fromUID + ", proposalID " + proposalID + ", acceptedValue " + acceptedValue);
		if (isComplete())
			return;

		ProposalID oldPID = acceptors.get(fromUID);
		
		// if it is an old proposal, just ignore this message
		if (oldPID != null && !proposalID.isGreaterThan(oldPID)) {
			log.debug("receiveAccepted: I'm true: oldPID != null && !proposalID.isGreaterThan(oldPID)");
			return;
		}
		
		// otherwise, add it to the acceptors
		acceptors.put(fromUID, proposalID);

		if (oldPID != null) {
			Proposal oldProposal = proposals.get(oldPID);
			oldProposal.retentionCount -= 1;
			if (oldProposal.retentionCount == 0)
				proposals.remove(oldPID);
		}
        
		if (!proposals.containsKey(proposalID))
			proposals.put(proposalID, new Proposal(0, 0, acceptedValue));

		Proposal thisProposal = proposals.get(proposalID);	
		
		thisProposal.acceptCount    += 1;
		thisProposal.retentionCount += 1;
        
        if (thisProposal.acceptCount == quorumSize) {
        	finalProposalID = proposalID;
        	finalValue      = acceptedValue;
        	proposals.clear();
        	acceptors.clear();
        	
        	messenger.onResolution(proposalID, acceptedValue);
			// TODO remove?
			finalValue = null;
        }
	}

	public int getQuorumSize() {
		return quorumSize;
	}

	@Override
	public Object getFinalValue() {
		return finalValue;
	}

	@Override
	public ProposalID getFinalProposalID() {
		return finalProposalID;
	}
}
