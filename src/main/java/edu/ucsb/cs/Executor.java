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


    private String BANK_FILENAME;
    List<Transaction> pendingTransactions = new ArrayList<Transaction>();
    private int lastExecuted = 0;
    private Log log = LogFactory.getLog(Executor.class);

    private static final Executor instance = new Executor();

    public static Executor getInstance() {
        return instance;
    }

    private Executor(){
        PrintWriter out=null;
        BANK_FILENAME = "bank_" + Main.nodeNumber;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(BANK_FILENAME, true)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
        // read file and find out what is the last executed transaction
        lastExecuted = findLast();
        log.debug("Executor: Inside the constructor");
    }

    public synchronized void  enqueue(Transaction t){
        pendingTransactions.add(t);
        log.debug("enqueue: Added txn " + t);
    }

    public void run(){
        log.debug("run: I am executing");
        try {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Transaction t : pendingTransactions) {
                    if ((lastExecuted + 1) == t.getLineNumber()) {
                        applyTransaction(t);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void applyTransaction(Transaction t) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(BANK_FILENAME, true)));
            out.println(t.getDelta());
            lastExecuted++;
            pendingTransactions.remove(t);
            log.debug("applyTransaction: Added value " + t.getDelta() + " to the log file");
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
        log.debug("&&& findLast: Lines in this file : " + lines);
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
