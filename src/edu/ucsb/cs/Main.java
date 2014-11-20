package edu.ucsb.cs;

import java.util.Scanner;

public class Main {

    public static final int DEPOSIT = 1;
    public static final int WITHDRAW = 2;
    public static final int BALANCE = 3;
    public static final int FAIL = 4;
    public static final int UNFAIL = 5;


    public static void main(String[] args) {

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
                            System.out.println("Depositing " +  amount);
                            // TODO
                            System.out.println(amount + " deposited");
                        }else {
                            System.out.println("Please behave.");
                            sc.next();
                        }
                        break;
                    case WITHDRAW:
                        System.out.println("Action not supported");
                        break;
                    case BALANCE:
                        System.out.println("Action not supported");
                        break;
                    case FAIL:
                        System.out.println("Action not supported");
                        break;
                    case UNFAIL:
                        System.out.println("Action not supported");
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
