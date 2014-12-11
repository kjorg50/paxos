package edu.ucsb.cs;

import edu.ucsb.cs.thrift.ThriftProposalID;
import edu.ucsb.cs.thrift.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by nevena on 12/10/14.
 */
public class Executor implements Runnable{


    List<Transaction> pendingTransactions = new ArrayList<Transaction>();
    int lastExecuted = 0;



    public synchronized void  enqueue(Transaction t){
        pendingTransactions.add(t);
    }

    public void run(){

        try {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Transaction t : pendingTransactions) {
                    if ((lastExecuted + 1) == t.getLineNumber()) {
                        // execute
                        lastExecuted++;
                        pendingTransactions.remove(t);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
