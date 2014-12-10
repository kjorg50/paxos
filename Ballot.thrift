namespace java edu.ucsb.cs.thrift

typedef i64 int

struct ProposalID{
    1:int ballotNumber,
    2:string uid
}

service Ballot {

    /** ProposalID == (int ballotNumber, string uid) **/


    /* Prepare
     * 1. my node ID
     * 2. the Proposal
     */
    oneway void prepare(1:string myId, 2:ProposalID propID)

    /* Promise
     * 1. my node ID
     * 2. the current Proposal
     * 3. the previous Proposal
     * 4. promised value
     */
    oneway void promise(1:string myId, 2:ProposalID propID, 3:ProposalID prevPropId, 4:int acceptedValue)

    /* Accept
     *
     */
    oneway void accept(1:string myId, 2:ProposalID propID, 3:int acceptedValue)

    /* Accepted
     *
     */
    oneway void accepted(1:string myId, 2:ProposalID propID, 3:int acceptedValue)

	// if accepted received from majority => onResolution
    oneway void decide(1:ProposalID propID, 2:int value)

    oneway void prepareNACK(1:string myId, 2:ProposalID propID, 3:ProposalID promisedID)
    oneway void acceptNACK(1:string myId, 2:ProposalID propID, 3:ProposalID promisedID)

    oneway void heartbeat(1:string myId, 2:ProposalID leaderPropID)

    list<int> update(1:int lastAcceptedBallot)
}
