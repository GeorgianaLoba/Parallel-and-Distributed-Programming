package repository;

import domain.Account;
import domain.Operation;
import domain.exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
    ArrayList<Account> accounts;
    Integer currentSerialNumber;
    ReentrantLock counterLock = new ReentrantLock(true); // enable fairness policy


    public Bank() {
        currentSerialNumber = 0;
        accounts = new ArrayList<>();
    }

    public Bank(ArrayList<Account> accounts) {
        currentSerialNumber = 0;
        this.accounts = accounts;
    }

    public synchronized void incrementSerialNumber(){
        currentSerialNumber+=1;
    }
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public void add(Account account){
        this.accounts.add(account);
    }

    public Integer getCurrentSerialNumber() {
        return currentSerialNumber;
    }

    public void setCurrentSerialNumber(Integer currentSerialNumber) {
        this.currentSerialNumber = currentSerialNumber;
    }

    public ArrayList<Integer> consistencyCheck(){
        ArrayList<Integer> inconsistentAccountsIds = new ArrayList<>();
        for (Account account: accounts) {
            if (!account.consistency()){
                inconsistentAccountsIds.add(account.getAccountId());
            }
        }
        return inconsistentAccountsIds;
    }

    public synchronized Account findAccount(Integer id){
        Optional<Account> account = accounts.stream().filter(a -> a.getAccountId().equals(id)).findFirst();
        if (account.isEmpty()) throw new RepositoryException("TRANSACTION FAILED: The account doesn't exist.");
        return account.get();
    }

    public synchronized void  transfer(Integer fromId, Integer toId, Integer amount){
        Account fromAccount = findAccount(fromId);
        Account toAccount = findAccount(toId);
        counterLock.lock();
        Operation operation1 = new Operation (currentSerialNumber, amount, fromId, toId);
        counterLock.unlock();
        counterLock.lock();
        Operation operation2 = new Operation (currentSerialNumber, amount, fromId, toId);
        counterLock.unlock();
        incrementSerialNumber();
        if (fromAccount.getAmount()<amount) throw new RepositoryException("TRANSACTION FAILED: The account from which you want to extract money doesn't have sufficient funds.");
        fromAccount.subtract(amount);
        fromAccount.logOperation(operation1);
        toAccount.add(amount);
        toAccount.logOperation(operation2);
    }
}
