package edu.ucsb.cs;

import edu.ucsb.cs.thrift.ThriftProposalID;
import edu.ucsb.cs.thrift.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by nevena on 12/10/14.
 */
public class Executor implements Runnable{


    private static final String BANK_FILENAME = "bank";
    List<Transaction> pendingTransactions = new ArrayList<Transaction>();
    private int lastExecuted = 0;
    private Log log = LogFactory.getLog(Executor.class);

    Executor(){
        PrintWriter out=null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(BANK_FILENAME, true)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
        // read file and find out what is the last executed transaction
        lastExecuted = findLast();
    }

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
                        applyTransaction(t);
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

    private void applyTransaction(Transaction t) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(BANK_FILENAME, true)));
            out.println(t.getDelta());
        } catch (IOException e) {
            log.error("applyTransaction: Exception writing the file." + e.getMessage());
        } finally {
            out.close();
        }
    }

    public int getBalance(){
        BufferedReader reader = null;
        String line;
        int balance = 0;
        // money from the file
        try {
            reader = new BufferedReader(new FileReader(BANK_FILENAME));
            while ((line = reader.readLine()) != null) {
                balance += Integer.parseInt(line);
            }
        } catch (IOException e) {
            log.error("getBalance: " + e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("getBalance: " + e.getMessage());
            }
        }
        // money from the queue
        for (Transaction t : pendingTransactions) {
            balance += t.getDelta();
        }
        return balance;
    }

    public int getLastExecuted() {
        return lastExecuted;
    }

    private int findLast(){
        BufferedReader reader = null;
        int lines = 0;
        try {

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(BANK_FILENAME, true)));
            reader = new BufferedReader(new FileReader(BANK_FILENAME));
            while (reader.readLine() != null) {
                lines++;
            }
        } catch (IOException e) {
            log.error("findLast: " + e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("findLast: " + e.getMessage());
            }
        }
        return lines;
    }

    public void print(){
        BufferedReader reader = null;
        String line;
        System.out.println("************ Bank statement ************************");
        try {
            reader = new BufferedReader(new FileReader(BANK_FILENAME));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            log.error("print: " + e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("print: " + e.getMessage());
            }
        }
        System.out.println("************ End of Bank statement *****************");
    }


}
