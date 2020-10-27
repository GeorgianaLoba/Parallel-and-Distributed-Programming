package ui;

import service.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class Console {
    public Service service;

    public Console(Service service) {
        this.service = service;
    }

    public void statistic(Integer numberOfBankAccounts, Integer numberOfThreads, Integer numberOfOperations){
        service.initialiseBankWithAccounts(numberOfBankAccounts);
        long start = System.currentTimeMillis();
        service.runTransfers(numberOfBankAccounts, numberOfThreads, numberOfOperations);
        Timer timer = new Timer(10000, arg0 -> {
            ArrayList<Integer> consistencies = service.consistencyCheck();
            System.out.println("From "+ numberOfBankAccounts +" accounts, " + consistencies.size() + " present inconsistencies.");
            System.out.println("The faulty accounts have the following ids: " + consistencies);
        });
        timer.setRepeats(true);
        timer.start();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("It took about " + timeElapsed + " milliseconds for this experiment!");
        System.out.println("Number of bank accounts: " + numberOfBankAccounts);
        System.out.println("Number of threads: " + numberOfThreads);
        System.out.println("Number of operations: " + numberOfOperations);
    }

    public void perform(){
        statistic(10, 3, 10);
        System.out.println("------------------------------------------------!");
        statistic(100, 10, 1000);
        System.out.println("------------------------------------------------!");
        statistic(100, 100, 1000);
        System.out.println("------------------------------------------------!");

//        statistic(10000, 10, 1000);
//        System.out.println("------------------------------------------------!");
//
//        statistic(10000, 10, 100000);
//        System.out.println("------------------------------------------------!");
//
//        statistic(10000, 10000, 10000);
//        System.out.println("------------------------------------------------!");
//
//        statistic(1000000, 10, 10000);
//        System.out.println("------------------------------------------------!");

    }

}
