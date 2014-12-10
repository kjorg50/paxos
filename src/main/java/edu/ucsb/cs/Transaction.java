package edu.ucsb.cs;

/**
 * Created by kylejorgensen on 12/9/14.
 */
public class Transaction {
    private Double previousAmount;
    private Double newAmount;

    public Transaction(Double prev, Double newAmt){
        this.previousAmount = prev;
        this.newAmount = newAmt;
    }

    public Double getPreviousAmount(){
        return previousAmount;
    }

    public Double getAmount(){
        return newAmount;
    }

    public void setNewAmount(Double amount){
        newAmount = amount;
    }
}
