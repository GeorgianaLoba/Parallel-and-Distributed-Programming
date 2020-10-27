package com.company;

public class Consumer implements Runnable {
    private SharedData sharedData;
    private Integer sum;
    private Integer totalElems;
    private Integer extractedElems;

    public Consumer(SharedData sharedData, Integer totalElems) {
        this.sharedData = sharedData;
        this.sum = 0;
        this.extractedElems = 0;
        this.totalElems = totalElems;
    }

    public Integer getSum(){
        return this.sum;
    }
    @Override
    public void run() {
        while (extractedElems<totalElems) {
            Integer received = sharedData.get();
            if (received != null) {
                sum += received;
                extractedElems+=1;
            }
        }
        System.out.println("consumer job done...");
    }
}
