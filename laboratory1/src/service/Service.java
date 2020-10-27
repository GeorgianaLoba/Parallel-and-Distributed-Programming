package service;

import domain.Account;
import repository.Bank;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Service {
    Bank bank;

    public Service(Bank bank) {
        this.bank = bank;
    }

    public void initialiseBankWithAccounts(Integer numberOfAccounts){
        Random rd = new Random();
        for (Integer i=0; i<numberOfAccounts;i++){
            Account account = new Account(i, rd.nextInt(1000));
            bank.add(account);
        }
    }

    public synchronized void transfer(Integer numberOfAccounts){
        Random rd = new Random();
        Integer from = rd.nextInt(numberOfAccounts) + 1;
        Integer to = rd.nextInt(numberOfAccounts) + 1;
        while (from.equals(to)) to = rd.nextInt(numberOfAccounts);
        Integer amount = rd.nextInt(100);
        bank.transfer(from, to, amount);
    }

    public ArrayList<Integer> consistencyCheck(){
        return bank.consistencyCheck();
    }

    public void runTransfers(Integer numberOfAccounts, Integer numberOfThreads, Integer numberOfOperations){
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        ArrayList<Callable<String>> tasks = new ArrayList<>();
        Integer operationCounter = 0;
        while (operationCounter < numberOfOperations){
            Callable<String> callableTask = () -> {
                transfer(numberOfAccounts);
                return "";
            };
            tasks.add(callableTask);
            operationCounter +=1;
        }
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        ArrayList<Integer> consistencies = consistencyCheck();
        System.out.println("From "+ numberOfAccounts +" accounts, " + consistencies.size() + " present inconsistencies.");
        System.out.println("The faulty accounts have the following ids: " + consistencies);
    }

}
