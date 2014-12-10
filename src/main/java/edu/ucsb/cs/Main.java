package edu.ucsb.cs;

import java.util.Scanner;

import edu.ucsb.cs.thrift.ThriftClient;
import edu.ucsb.cs.thrift.ThriftServer;
import org.apache.log4j.Logger;

/**
 * Main class of our Paxos implementation. Handles the command line interaction with the user.
 */
public class Main {

    public static final int DEPOSIT = 1;
    public static final int WITHDRAW = 2;
    public static final int BALANCE = 3;
    public static final int FAIL = 4;
    public static final int UNFAIL = 5;
    public static int node;

    static Logger logger = Logger.getLogger(Main.class);
    //final static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);
    public static PaxosHandler handler;

    public static void main(String[] args) {

        int node=0;
        if(args.length == 1){
            node = Integer.parseInt(args[0]);
            System.out.println("Setting up node: " + node);
        } else{
            System.out.println("Usage:  java -cp target/paxos-0.0.1.jar edu.ucsb.cs.Main <NODENUM>");
            System.exit(1);
        }

        handler = new PaxosHandler(node);
        System.out.println(" Deposit \t\t 1 \n Withdraw \t\t 2 \n Balance \t\t 3 \n Fail \t\t\t 4 \n Unfail \t\t 5 \n");
        Scanner sc = new Scanner(System.in);

        while (true) {
            if (sc.hasNextInt()) {
                int action = -1;
                action = sc.nextInt();

                switch (action) {
                    case DEPOSIT:
                        System.out.println("Type amount to deposit:");
                        if (sc.hasNextDouble()) {
                            Double amount = sc.nextDouble();
                            logger.info("Depositing " + amount);
                            handler.deposit(amount);
                            logger.info(amount + " deposited");

                            ThriftServer.startThriftServer();
                            ThriftClient.callClient();
                        } else {
                            System.out.println("Please behave.");
                            sc.next();
                        }
                        break;
                    case WITHDRAW:
                        System.out.println("Type amount to withdraw:");
                        if (sc.hasNextDouble()) {
                            Double amount = sc.nextDouble();
                            logger.info("Withdrawing " + amount);
                            handler.withdraw(amount);
                            logger.info(amount + " withdrawn");
                        } else {
                            System.out.println("Please behave.");
                            sc.next();
                        }
                        break;
                    case BALANCE:
                        System.out.printf("Your account balance is $%.2f\n", handler.getBalance() );
                        break;
                    case FAIL:
                        handler.fail();
                        System.out.println("Failure ON");
                        break;
                    case UNFAIL:
                        handler.unfail();
                        System.out.println("Failure OFF");
                        break;
                    default:
                        System.out.println("Action not supported");
                        break;
                }
                System.out.println(" Deposit \t\t 1 \n Withdraw \t\t 2 \n Balance \t\t 3 \n Fail \t\t\t 4 \n Unfail \t\t 5 \n");
            } else {
                System.out.println("Please behave.");
                sc.next();
            }
        }
    }
}