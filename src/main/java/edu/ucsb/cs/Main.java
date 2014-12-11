package edu.ucsb.cs;

import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutorService;

import cocagne.paxos.functional.HeartbeatNode;
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
    public static final int MAJORITY=3; //TODO quorum size
    public static String nodeNumber;
    private Log log = LogFactory.getLog(Main.class);
    public static PaxosHandler handler;
    private PaxosMessengerImpl messenger;
    private HeartbeatNode heartbeatNode;
    private Stack<Transaction> transactions;
    private Double balance;
    private Executor executor;




    public void init(String nodeUID) {
        ExecutorService es = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor = new Executor(nodeUID);
        es.submit(executor);

        messenger = new PaxosMessengerImpl(nodeUID, executor);
        heartbeatNode = new HeartbeatNode(messenger,nodeUID,MAJORITY,null,1000,5000);
        transactions = new Stack<Transaction>();
        balance = 0.0;

        ThriftServer.startThriftServer(heartbeatNode, nodeNumber);

        System.out.println(" Deposit \t\t 1 \n Withdraw \t\t 2 \n Balance \t\t 3 \n Fail \t\t\t 4 \n Unfail \t\t 5\n Print \t\t 6\n");
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
                            log.info("Depositing " + amount);
                            deposit(amount);
                            // log.info(amount + " deposited");

                        } else {
                            System.out.println("Please behave.");
                            sc.next();
                        }
                        break;
                    case WITHDRAW:
                        System.out.println("Type amount to withdraw:");
                        if (sc.hasNextInt()) {
                            Integer amount = sc.nextInt();
                            log.info("Withdrawing " + amount);
                            withdraw(amount);
                            // log.info(amount + " withdrawn");
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
                        " Print \t\t 6\n");
            } else {
                System.out.println("Please behave.");
                sc.next();
            }
        }
    }

    public void deposit(int amount){
        Transaction transaction = new Transaction(executor.getLastExecuted()+1, amount);
        heartbeatNode.setProposal(transaction);
        heartbeatNode.prepare(); // run paxos
        return;
    }

    public void withdraw(int amount){
        Transaction transaction = new Transaction(executor.getLastExecuted()+1, -amount);
        heartbeatNode.setProposal(transaction);
        heartbeatNode.prepare(); // run paxos
        return;
    }

    public int getBalance(){
       return executor.getBalance();
    }

    public void fail(){
        heartbeatNode.setActive(false);
        log.info("fail: Enter FAIL state");
    }

    public void unfail(){
        heartbeatNode.setActive(true);
        log.info("unfail: Exit FAIL state");
    }

    public void print(){
        executor.print();
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