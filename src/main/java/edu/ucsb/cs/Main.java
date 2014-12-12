package edu.ucsb.cs;

import java.util.Scanner;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import cocagne.paxos.functional.HeartbeatNode;
import edu.ucsb.cs.thrift.BallotHandler;
import edu.ucsb.cs.thrift.ThriftClient;
import edu.ucsb.cs.thrift.ThriftServer;
import edu.ucsb.cs.thrift.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main class of our Paxos implementation. Handles the command line interaction with the user.
 */
public class Main {

    public static final int DEPOSIT = 1;
    public static final int WITHDRAW = 2;
    public static final int BALANCE = 3;
    public static final int FAIL = 4;
    public static final int UNFAIL = 5;
    public static final int PRINT = 6;
    public static String nodeNumber;
    private Log log = LogFactory.getLog(Main.class);
    //private PaxosMessengerImpl messenger;
    //private HeartbeatNode heartbeatNode;

    public static boolean FAILING = false;

    public void init(String nodeUID) {
        ExecutorService es = java.util.concurrent.Executors.newSingleThreadExecutor();
        es.submit(Executor.getInstance());

        //messenger = new PaxosMessengerImpl(nodeUID, Executor.getInstance());
        //heartbeatNode = new HeartbeatNode(messenger,nodeUID,MAJORITY,null,1000,5000);

        ThriftServer.startThriftServer(nodeNumber);

        System.out.println(" Deposit \t\t 1 \n Withdraw \t\t 2 \n Balance \t\t 3 \n Fail \t\t\t 4 \n Unfail \t\t 5\n Print \t\t\t 6\n");
        Scanner sc = new Scanner(System.in);

        while (true) {
            if (sc.hasNextInt()) {
                int action = -1;
                action = sc.nextInt();

                switch (action) {
                    case DEPOSIT:
                        System.out.println("Type amount to deposit:");
                        if (sc.hasNextInt()) {
                            Integer amount = sc.nextInt();
                            deposit(amount);

                        } else {
                            System.out.println("Please behave.");
                            sc.next();
                        }
                        break;
                    case WITHDRAW:
                        System.out.println("Type amount to withdraw:");
                        if (sc.hasNextInt()) {
                            Integer amount = sc.nextInt();
                            withdraw(amount);

                        } else {
                            System.out.println("Please behave.");
                            sc.next();
                        }
                        break;
                    case BALANCE:
                        System.out.printf("Your account balance is $%d\n", getBalance());
                        break;
                    case FAIL:
                        fail();
                        System.out.println("Failure ON");
                        break;
                    case UNFAIL:
                        unfail();
                        System.out.println("Failure OFF");
                        break;
                    case PRINT:
                        print();
                    default:
                        System.out.println("Action not supported");
                        break;
                }
                System.out.println(" Deposit \t\t 1 \n Withdraw \t\t 2 \n Balance \t\t 3 \n Fail \t\t\t 4 \n Unfail \t\t 5 \n" +
                        " Print \t\t\t 6\n");
            } else {
                System.out.println("Please behave.");
                sc.next();
            }
        }
    }

    public void deposit(int amount){
        if (!FAILING)
            startPaxos(amount);
        else
            System.out.println("In FAIL state");
    }

    public void withdraw(int amount){
        if (!FAILING)
            startPaxos(-1 * amount);
        else
            System.out.println("In FAIL state");
    }

    private void startPaxos(int amount) {
        int currentBal = Executor.getInstance().getBalance();
        if (amount + currentBal < 0) {
            System.out.println("Cannot fulfill request -- Balance: " + currentBal + "; Delta: " + amount);
            return;
        }
        String txnId = "Txn_" + Executor.getInstance().nextLineNumber();
        log.info("Starting txn: " + txnId);
        Transaction transaction = new Transaction(Executor.getInstance().getLastExecuted()+1, amount);
        HeartbeatNode hbn = HBNStore.getInstance().getNode(txnId, nodeNumber);
        hbn.setProposal(transaction);
        hbn.prepare(); // run paxos
    }

    public int getBalance(){
       return Executor.getInstance().getBalance();
    }

    public void fail(){
        FAILING = true;
        log.info("fail: Enter FAIL state");
    }

    public void unfail(){
        FAILING = false;
        log.info("unfail: Exit FAIL state");
    }

    public void print(){
        Executor.getInstance().print();
    }

    public static void main(String[] args){
        Main mainClass = new Main();

        if(args.length == 1){
            nodeNumber = args[0];
            System.out.println("Main: Setting up node: " + nodeNumber);
        } else{
            System.out.println("Usage:  java -cp target/paxos-0.0.1.jar edu.ucsb.cs.Main <nodeNumberNUM>");
            System.exit(1);
        }

        mainClass.init(nodeNumber);
    }

}