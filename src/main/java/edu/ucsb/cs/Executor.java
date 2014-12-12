package edu.ucsb.cs;

import edu.ucsb.cs.thrift.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        lastExecuted = getNumberOfLinesInFile();
        log.debug("Executor: Inside the constructor");
    }

    public synchronized void enqueue(Transaction t){
        pendingTransactions.add(t);
        this.notifyAll();
        log.debug("enqueue: Added txn " + t);
    }

    public void run(){
        log.debug("run: I am executing");
        try {
            while(true) {
                synchronized (this) {
                    if (pendingTransactions.isEmpty()) {
                        this.wait(500);
                    } else {
                        for (Transaction t : pendingTransactions) {
                            if ((lastExecuted + 1) == t.getLineNumber()) {
                                applyTransaction(t);
                                break;
                            }
                        }
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
            lastExecuted++;
            pendingTransactions.remove(t);
            log.debug("applyTransaction: Added value " + t.getDelta() + " to the log file");
        } catch (IOException e) {
            log.error("applyTransaction: Exception writing the file." + e.getMessage());
        } finally {
            out.close();
        }
    }

    public synchronized int getBalance(){
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

    /**
     * Determine the next line number to be voted upon in our log. It not only checks the current log file, but also
     * checks the pending transactions in the queue.
     *
     * @return the next open line number in the log
     */
    public synchronized int nextLineNumber() {
        int last = getNumberOfLinesInFile();
        for (Transaction t : pendingTransactions) {
            if (t.getLineNumber() > last) {
                last = t.getLineNumber();
            }
        }
        return last + 1;
    }

    public int getLastExecuted() {
        return lastExecuted;
    }

    private int getNumberOfLinesInFile(){
        BufferedReader reader = null;
        int lines = 0;
        try {

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(BANK_FILENAME, true)));
            reader = new BufferedReader(new FileReader(BANK_FILENAME));
            while (reader.readLine() != null) {
                lines++;
            }
        } catch (IOException e) {
            log.error("getNumberOfLinesInFile: " + e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("getNumberOfLinesInFile: " + e.getMessage());
            }
        }
        log.debug("getNumberOfLinesInFile: Lines in this file : " + lines);
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
