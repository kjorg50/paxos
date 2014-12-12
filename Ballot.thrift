namespace java edu.ucsb.cs.thrift

typedef i32 int

/** ThriftProposalID == (int ballotNumber, string uid) **/
struct ThriftProposalID{
    1:int ballotNumber,
    2:string uid
}

struct Transaction{
    1:int lineNumber,
    2:int delta
}


service Ballot {

    /* Prepare
     * 1. my node ID
     * 2. the Proposal
     */
    oneway void prepare(1:string myId, 2:ThriftProposalID propID)

    /* Promise
     * 1. my node ID
     * 2. the current Proposal
     * 3. the previous Proposal
     * 4. promised value
     */
    oneway void promise(1:string myId, 2:ThriftProposalID propID, 3:ThriftProposalID prevPropId, 4:Transaction acceptedValue)

    /* Accept
     *
     */
    oneway void accept(1:string myId, 2:ThriftProposalID propID, 3:Transaction acceptedValue)

    /* Accepted
     *
     */
    oneway void accepted(1:string myId, 2:ThriftProposalID propID, 3:Transaction acceptedValue)

    oneway void prepareNACK(1:string myId, 2:ThriftProposalID propID, 3:ThriftProposalID promisedID)
    oneway void acceptNACK(1:string myId, 2:ThriftProposalID propID, 3:ThriftProposalID promisedID)

    oneway void heartbeat(1:string myId, 2:ThriftProposalID leaderPropID)

    list<Transaction> update(1:int lastAcceptedBallot)
}
