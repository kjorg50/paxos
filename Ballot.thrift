namespace java edu.ucsb.cs.thrift

typedef i64 int

service Ballot {

    oneway void prepare(1:int ballotNumber, 2:int myId)
    oneway void ack(1:int ballotNumber, 2:int acceptedNumber, 3:int acceptedVal)
    oneway void accept(1:int ballotNumber, 2:int leaderVal)
    oneway void accepted(1:int ballotNumber, 2:int val)
	    // if accepted received from majority => decide
    oneway void decide(1:int ballotNumber, 2:int value)
    list<int> update(1:int lastAcceptedBallot)
    bool isLeader()
}
