package com.company.mpi.primes;
//
//import mpi.MPI;
//
//import static com.company.mpi.primes.Utils.initVector;
//
//public class Main {
//    public static void main(String[] args)  {
//        MPI.Init(args);
//        Integer nrProc = MPI.COMM_WORLD.Size();
//        if (MPI.COMM_WORLD.Rank()==0){
//            //int[] vector1 = initVector();
//            int[] vector1 = {1,2,3,4,5,6,7,8};
//            //int[] vector2 = initVector();
//            int[] vector2 = {1,2,3,4,5,6,7,8};
//            int[] primes = Utils.primes(vector1, vector2, nrProc);
//            for (int i=0;i<primes.length;i++){
//                System.out.println(primes[i]);
//            }
//        }
//        else{
//            Utils.worker(MPI.COMM_WORLD.Rank(), nrProc);
//        }
//        MPI.Finalize();
//    }
//
//}
import mpi.MPI;

import java.util.concurrent.ExecutionException;


public class Main {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int nrProc = MPI.COMM_WORLD.Size();

        int[][] a = {{1, 1, 1}, {2, 5, 2}, {3, 3, 3}};
        int[][] b = {{1, 1, 1}, {2, 4, 2}, {3, 3, 3}};

        if (rank == 0) {
            int[][] rez = matrixSum(a, b, nrProc);

            int nrRows = a.length;
            int nrCols = a[0].length;
            for (int i = 0; i < nrRows; i++) {
                for (int j = 0; j < nrCols; j++) {
                    System.out.print(rez[i][j]);
                    System.out.print(' ');
                }
                System.out.print("\n");
            }
        }
        else {
            worker(rank, nrProc);
        }

        MPI.Finalize();
    }

    static int _recvInt(int processID, int tag){
        int[] intArr = new int[1];
        MPI.COMM_WORLD.Recv(intArr, 0, 1, MPI.INT, processID, tag);
        return intArr[0];
    }

    static int[] _recvArr(int length, int processID, int tag){
        int[] intArr = new int[length];
        MPI.COMM_WORLD.Recv(intArr, 0, length, MPI.INT, processID, tag);
        return intArr;
    }

    static int[][] matrixSum(int[][] a, int[][] b, int nrProcs) {
        nrProcs--; // Because process 0 is the main function
        int nrRows = a.length;
        int nrCols = a[0].length;
        int[][] rez = new int[nrRows][nrCols];
        int[] rezVector = new int[nrRows * nrCols];
        int[] aVector = new int[nrRows * nrCols];
        int[] bVector = new int[nrRows * nrCols];

        // Flatten the 2 input matrices into vectors
        int vectorDim = 0;
        for (int i = 0; i < nrRows; i++)
            for (int j = 0; j < nrCols; j++) {
                aVector[vectorDim] = a[i][j];
                bVector[vectorDim] = b[i][j];
                vectorDim++;
            }

        int begin = 0;
        if (nrProcs > vectorDim)
            nrProcs = vectorDim;  // Ensure we don't have more processes than elements to process
        final int stepSize = vectorDim / nrProcs;

        // Send dimensions and vectors to workers
        for (int processID = 1; processID <= nrProcs; processID++) {
            int end;
            if (processID == nrProcs)
                end = vectorDim;
            else
                end = begin + stepSize;

            MPI.COMM_WORLD.Issend(new int[]{vectorDim}, 0, 1, MPI.INT, processID, 1);
            MPI.COMM_WORLD.Issend(new int[]{begin}, 0, 1, MPI.INT, processID, 2);
            MPI.COMM_WORLD.Issend(new int[]{end}, 0, 1, MPI.INT, processID, 3);
            MPI.COMM_WORLD.Issend(aVector, 0, vectorDim, MPI.INT, processID, 4);
            MPI.COMM_WORLD.Issend(bVector, 0, vectorDim, MPI.INT, processID, 5);

            begin = end;
        }

        // Get back vectors from workers
        for (int processID = 1; processID <= nrProcs; processID++) {
            int [] processRezVector = _recvArr(vectorDim, processID, 6);
            for (int i = 0; i < vectorDim; i++) {
                rezVector[i] += processRezVector[i];
            }
        }

        // Recreate the original matrix
        for (int i = 0; i < vectorDim; i++) {
            rez[i / nrRows][i % nrCols] = rezVector[i];
        }
        return rez;
    }

    static void worker(int myId, int nrProcs) {
        int dim = _recvInt(0, 1);
        int begin = _recvInt(0, 2);
        int end = _recvInt(0, 3);

        int[] aVector = _recvArr(dim, 0, 4);
        int[] bVector = _recvArr(dim, 0, 5);
        int[] rezVector = new int[dim];

        for (int i = begin; i < end; i++) {
            rezVector[i] = aVector[i] + bVector[i];
        }

        MPI.COMM_WORLD.Issend(rezVector, 0, dim, MPI.INT, 0, 6);
    }
}
