package com.company;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedData {
    private Queue<Integer> content;
    private ReentrantLock lock;
    private Condition condition;
    private Integer size;

    public SharedData(Integer size, ReentrantLock lock) {
        this.content = new LinkedList<>();
        this.size = size;
        this.lock = lock;
        this.condition = lock.newCondition();
    }

    public Integer getSize(){
        return this.content.size();
    }

    public void put(Integer element){
        lock.lock();
        try{
            while (content.size()==size){
                condition.await();
            }
            content.add(element);
            condition.signalAll();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public Integer get(){
        lock.lock();
        try{
            while (content.isEmpty()){
                condition.await();
            }
            Integer elem = content.remove();
            condition.signalAll();
            return elem;
        }
        catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
        finally {
            lock.unlock();
        }
    }
}
