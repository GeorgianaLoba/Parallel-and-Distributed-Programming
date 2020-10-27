package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args){

        Integer VECTOR_SIZE = 10;
        Integer SHARED_DATA_SIZE = 5;

        List<Integer> firstVector = new ArrayList<>();
        List<Integer> secondVector = new ArrayList<>();
        for(int i=1;i<=VECTOR_SIZE;i++){
            firstVector.add(new Random().nextInt(500));
            secondVector.add(new Random().nextInt(500));
        }

        ReentrantLock lock = new ReentrantLock();
        SharedData sharedData = new SharedData(SHARED_DATA_SIZE, lock);
        Consumer consumer = new Consumer(sharedData, VECTOR_SIZE);
        Producer producer = new Producer(sharedData, firstVector, secondVector);


        Thread consumerThread = new Thread(consumer);
        Thread producerThread = new Thread(producer);
        consumerThread.start();
        producerThread.start();
        try {
            consumerThread.join();
            producerThread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Computed sum of the vectors is: " + consumer.getSum());

        long sum = 0;
        for (int i = 0; i < firstVector.size(); i++)
            sum += firstVector.get(i) * secondVector.get(i);
        System.out.println("The product of the vectors computed sequentially is: " + sum);
    }
}
