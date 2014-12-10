namespace java edu.ucsb.cs.thrift

typedef i64 int

service Ballot {

    /** ProposalID == (int ballotNumber, string uid) **/
    struct ProposalID{
    1:int ballotNumber,
    2:string uid
    }

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
    oneway void accept(1:string myId, 2:Proposal propID, 3:int acceptedValue)

    /* Accepted
     *
     */
    oneway void accepted(1:string myId, 2:Proposal propID, 3:int acceptedValue)

	// if accepted received from majority => onResolution
    oneway void decide(1:Proposal propID, 2:int value)

    oneway void prepareNACK(1:string myId, 2:Proposal propID, 3:Proposal promisedID)
    oneway void acceptNACK(1:string myId, 2:Proposal propID, 3:Proposal promisedID)

    oneway void heartbeat(1:string myId, 2:Proposal leaderPropID)

    list<int> update(1:int lastAcceptedBallot)
}
