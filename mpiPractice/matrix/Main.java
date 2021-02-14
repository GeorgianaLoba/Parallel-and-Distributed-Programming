package com.company.mpi.matrix;

import mpi.MPI;

public class Main {
    public static void main(String[] args)  {
        MPI.Init(args);
        Integer nrProc = MPI.COMM_WORLD.Size();
        if (MPI.COMM_WORLD.Rank()==0){
            int[][] a = Utils.initMatrix();
            int[][] b = Utils.initMatrix();
            int[][] result =  Utils.primes(a, b, nrProc);
            Utils.printMatrix(result, a.length);
        }
        else{
            Utils.worker(MPI.COMM_WORLD.Rank(), nrProc);
        }
        MPI.Finalize();
    }
}
