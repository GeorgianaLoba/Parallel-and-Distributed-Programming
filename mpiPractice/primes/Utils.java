package com.company.mpi.primes;

import mpi.MPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Utils {
    public static int[] initVector(){
        int n=13;
        int[] vec = new int[n];
        Random random = new Random();
        for (int i=0;i<n;i++){
            vec[i] = random.nextInt(100);
        }
        return vec;
    }

    public static void computeOneElement(int[] vec1, int[] vec2, int begin, int end, int[]rez){
       int k=begin;
       while (k<end) {
           int val = 0;
        for (int i = 0; i < vec1.length; i++) {
            if (k>=i) {
                val += vec1[i] * vec2[k - i];
            }
        }
        rez[k] = val;
        k++;
    }
    }


    public static int[] primes(int [] vec1, int[] vec2, Integer nrProc) {
        int[] metadata = new int[2];
        metadata[0] = vec1.length;
        metadata[1] = vec2.length;
        MPI.COMM_WORLD.Bcast(metadata, 0, 2, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(vec1, 0, vec1.length, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(vec2, 0, vec2.length, MPI.INT, 0);
        int begin =0;
        int step = (vec1.length+nrProc)/ (nrProc-1);
        System.out.println(step);
        for (int i=1;i<nrProc;i++){
            int end = begin + step;
            if (end>(vec1.length)) end=vec1.length;
            int[] ranged = new int[2];
            ranged[0] = begin;
            ranged[1] = end;
            System.out.println("Compute in range "+ begin + "-" + end);
            MPI.COMM_WORLD.Issend(ranged, 0, 2, MPI.INT, i, 1);
            begin = end;
        }
        int[] convoVector = new int[vec1.length];
        for (int i=1;i<nrProc;i++){
            int[] range = new int[2];
            MPI.COMM_WORLD.Recv(range, 0, 2, MPI.INT, i, 2);
            int[] partialVector = new int[vec1.length];
            MPI.COMM_WORLD.Recv(partialVector, 0,vec1.length, MPI.INT, i, 2);
            if (range[1] - range[0] >= 0)
                System.arraycopy(partialVector, range[0], convoVector, range[0], range[1] - range[0]);
        }
        return convoVector;
    }

    public static void worker(int rank, Integer nrProc) {
        int[] metadata= new int[2]; //0 vec1 length, 1 vec2 length
        MPI.COMM_WORLD.Bcast(metadata,0, 2, MPI.INT, 0);
        int[] vec1 = new int[metadata[0]];
        int[] vec2 = new int[metadata[1]];
        MPI.COMM_WORLD.Bcast(vec1, 0, metadata[0], MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(vec2, 0, metadata[0], MPI.INT, 0);

        int[] range = new int[2];
        int[] convo = new int[metadata[0]];
        MPI.COMM_WORLD.Recv(range, 0, 2, MPI.INT, 0, 1);
        System.out.println("Range:"+ range[0] + "-" + range[1]);
        computeOneElement(vec1, vec2, range[0], range[1], convo);
        MPI.COMM_WORLD.Issend(range, 0, 2, MPI.INT, 0, 2);
        MPI.COMM_WORLD.Issend(convo, 0,metadata[0], MPI.INT, 0, 2);
    }

}
