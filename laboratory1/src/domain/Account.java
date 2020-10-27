package domain;

import java.util.ArrayList;

public class Account {
    Integer amount;
    Integer accountId;
    ArrayList<Operation> log;

    public Account(Integer accountId, Integer amount) {
        this.amount = amount;
        this.accountId = accountId;
        log = new ArrayList<>();
        log.add(new Operation(-1, amount, 0, accountId, OperationType.RECEIVE));
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public ArrayList<Operation> getLog() {
        return log;
    }

    public void setLog(ArrayList<Operation> log) {
        this.log = log;
    }

    public synchronized void subtract(Integer amount){
        this.amount-=amount;
    }

    public synchronized void add(Integer amount){
        this.amount+=amount;
    }

    public synchronized void logOperation(Operation operation){
        if (operation.getFromId().equals(this.accountId)) {
            operation.setOperationType(OperationType.SEND);
        }
        if (operation.getToId().equals(this.accountId)) {
            operation.setOperationType(OperationType.RECEIVE);
        }
        this.log.add(operation);
    }

    public Boolean consistency() {
        float computeAmount=0;
        for (Operation operation: log) {
            if (operation.operationType == OperationType.RECEIVE){
                computeAmount+=operation.amount;
            }
            else{
                computeAmount-=operation.amount;
            }
        }
        return computeAmount == amount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "amount=" + amount +
                ", accountId=" + accountId +
                ", log=" + log +
                "}\n";
    }
}
