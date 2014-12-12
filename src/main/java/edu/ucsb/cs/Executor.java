package edu.ucsb.cs;

import edu.ucsb.cs.thrift.Ballot;
import edu.ucsb.cs.thrift.ThriftProposalID;
import edu.ucsb.cs.thrift.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

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
    private MessengerConf conf;

    private static final Executor instance = new Executor();

    public static Executor getInstance() {
        return instance;
    }

    private Executor(){
        PrintWriter out=null;
        BANK_FILENAME = "bank_" + Main.nodeNumber;
        conf = new MessengerConf();
        // create the file if it does not exist
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(BANK_FILENAME, true)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
        log.debug("Executor: Inside the constructor");
        // read file and find out what is the last executed transaction
        lastExecuted = getNumberOfLinesInFile();
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

    /**
     * Called from the constructor. We want to check if there have been any transactions that have
     * occurred while this node was not available. If so, then we will add them to our log. This should
     * help us recover from failures.
     * @param previous the line number of the last item in our log
     */
    public void recoverLog(int previous){
        log.debug("recoverLog: My previous known log entry is line " + previous + ", recovering missing transactions...");

        for (int i = 0; i < conf.getMessengerConfigurations().size(); i++) {
            doRecoverLog(previous, i);
        }
    }

    private void doRecoverLog(int previous, int i) {
        List<Transaction> missedTransactions = new ArrayList<Transaction>();

        try {
            TTransport transport;
            Messenger oneMessenger = conf.getOneMessenger(i);
            log.debug("doRecoverLog: asking " + oneMessenger.getAddress() + " for its log information");
            transport = new TSocket(oneMessenger.getAddress(), oneMessenger.getPort());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Ballot.Client client = new Ballot.Client(protocol);

            missedTransactions = client.update(previous);
            log.debug("doRecoverLog: received " + missedTransactions.size() + " missed log entries from " + oneMessenger.getAddress());
            for( Transaction txn : missedTransactions){
                enqueue(txn);
            }

            transport.close();
        } catch (TTransportException ex) {
            log.error("doRecoverLog: Error recovering log. It is possible that the receiving node is not available");
        } catch (TException x) {
            log.error("doRecoverLog: Error recovering log.");
            x.printStackTrace();
        }
    }

    /**
     * This function is used to help another node recover the log. It returns a list of all the
     * Transactions in our log that have occurred after line 'last'.
     * @param last last known line in the log of the node who send this request
     * @return list of Transactions that have occurred in our log
     */
    public List<Transaction> sendRecovery(int last){
        List<Transaction> missedTransactions = new ArrayList<Transaction>();
        BufferedReader reader = null;
        String inputLine;
        int lineNum = 0;

        try {
            reader = new BufferedReader(new FileReader(BANK_FILENAME));
            while ((inputLine = reader.readLine()) != null) {
                lineNum++;
                if (lineNum > last){
                    Transaction t = new Transaction(lineNum,Integer.parseInt(inputLine));
                    missedTransactions.add(t);
                }
            }
        } catch (IOException e) {
            log.error("sendRecovery: error reading log file" + e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("sendRecovery: " + e.getMessage());
            }
        }

        return missedTransactions;
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
