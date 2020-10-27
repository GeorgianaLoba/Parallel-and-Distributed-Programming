package com.company;

import java.util.ArrayList;
import java.util.List;

public class Producer implements Runnable {
    private SharedData sharedData;
    List<Integer> vector1;
    List<Integer> vector2;

    public Producer(SharedData sharedData, List<Integer> vector1, List<Integer> vector2) {
        this.sharedData = sharedData;
        this.vector1 = vector1;
        this.vector2 = vector2;
    }

    @Override
    public void run() {
        for (int i = 0; i < vector1.size(); i++)
            sharedData.put(vector1.get(i) * vector2.get(i));
        System.out.println("producer job done...");
    }
}
