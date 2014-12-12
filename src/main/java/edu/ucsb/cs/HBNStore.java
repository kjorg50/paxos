package edu.ucsb.cs;

import cocagne.paxos.functional.HeartbeatNode;
import edu.ucsb.cs.thrift.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nevena on 12/11/14.
 */
public class HBNStore {

    public static final int MAJORITY = 3;

    private static final HBNStore instance = new HBNStore();

    private Map<String,HeartbeatNode> nodes = new ConcurrentHashMap<String,HeartbeatNode>();

    private HBNStore() {

    }

    public static HBNStore getInstance() {
        return instance;
    }

    /**
     * Get the HeartbeatNode object used for the given transaction ID. If there does not yet exist a HeartbeatMode for
     * a given transaction ID, then this method will create one and put it in the map of nodes
     * @param txnId the transaction ID that we are interested in
     * @param nodeNumber the physical node number in our system
     * @return A HeartbeatNode object that corresponds to txnId
     */
    public HeartbeatNode getNode(String txnId, String nodeNumber) {
        HeartbeatNode node = nodes.get(txnId);
        if (node == null) {
            PaxosMessengerImpl messenger = new PaxosMessengerImpl(nodeNumber, Executor.getInstance(), txnId);
            node = new HeartbeatNode(messenger, nodeNumber, MAJORITY,null,1000,5000);
            nodes.put(txnId, node);
        }
        return node;
    }

}
