package com.company.simpleParallel.productVectors;

import java.util.Random;
import java.util.concurrent.*;

public class Main {

    static int firstNDigits(int nr, int n) {
        return (int)(nr/Math.pow(10, n));
    }
    static int lastNDigits(int nr, int n) {
        return (int) (nr % Math.pow(10, n));
    }

    static Integer karatsuba(Integer A, Integer B){
        if(A.toString().length() < 2 || B.toString().length()<2) return A*B;
        int m = (Math.max(A.toString().length(), B.toString().length()) / 2);
        Integer lowA = firstNDigits(A, m);
        Integer highA = lastNDigits(A, m);
        Integer lowB = firstNDigits(B, m);
        Integer highB = lastNDigits(B, m);
        Integer result1 = karatsuba(lowA,lowB);
        Integer result2 = karatsuba(lowA+highA, lowB+highB);
        Integer result3 = karatsuba(highA, highB);
        Integer z1 = result1 * (int)Math.pow(10, 2*m);
        Integer z2 = (result2-result3-result1) * (int)Math.pow(10, m);
        return result3+z1+z2;
    }

    static Integer karatsubaParallel(int depth, Integer A, Integer B) throws InterruptedException, ExecutionException {
        if(A.toString().length() < 2 || B.toString().length()<2) return A*B;
        if (depth>4) return A*B;

        int m = (Math.max(A.toString().length(), B.toString().length()) / 2);

        Integer lowA = firstNDigits(A, m);
        Integer highA = lastNDigits(A, m);
        Integer lowB = firstNDigits(B, m);
        Integer highB = lastNDigits(B, m);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Callable<Integer> r1 = ()-> karatsubaParallel(depth+1, lowA,lowB);
        Callable<Integer> r2 = () -> karatsubaParallel(depth+1, lowA+highA, lowB+highB);
        Callable<Integer>  r3 = ()-> karatsubaParallel(depth+1, highA, highB);

        Future<Integer> f1 = executor.submit(r1);
        Future<Integer> f2 = executor.submit(r2);
        Future<Integer> f3 = executor.submit(r3);

        Integer result1 = f1.get();
        Integer result2 = f2.get();
        Integer result3 = f3.get();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        executor.shutdown();

        Integer z1 = result1 * (int)Math.pow(10, 2*m);
        Integer z2 = (result2-result3-result1) * (int)Math.pow(10, m);
        return result3+z1+z2;
    }

    public static void main(String[] args) {
        Random random = new Random();
        Integer A = random.nextInt(5000)+1000;
        Integer B = random.nextInt(5000)+1000;
        System.out.println(A*B);
        System.out.println(karatsuba(A,B));
        try {
            System.out.println(karatsubaParallel(1, A,B));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

}
