package com.company.mpi.matrix;

import mpi.MPI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public  class Utils {

    public static int[][] initMatrix() {
        //will create a matrix with random vals
        int n = 4;
        Random random = new Random();
        int[][]  matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = random.nextInt(100);
            }
        }
        return matrix;
    }

    public static void printMatrix(int[][] a, int n){
        //print a matrix prettily
        StringBuilder bd = new StringBuilder();
        for (int i=0;i<n;i++) {
            for (int j = 0; j < n; j++) {
                bd.append(a[i][j]);
                bd.append(" ");
            }
            bd.append("\n");
        }
        System.out.println(bd.toString());
    }
    public static int[][] transposeMatrix(int[][] original, int n){
        //transpose a matrix, each row -> col
        int[][] transpose = new int[n][n];
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                transpose[i][j] = original[j][i];
            }
        }
        return transpose;
    }
    public static void computeOneElement(int[] a, int[] b, int n, int row, int col, int[][] res){
        //compute the multiplication of a row with a column
        for (int i=0;i<n;i++){
            res[row][col] += a[i]*b[i];
        }
    }

    public static int[][] primes(int[][] a, int[][] b, Integer nrProc) {
        int nrRows = a.length;
        int[][] res = new int[nrRows][nrRows];

        int begin = 0;
        int step = (nrRows+nrProc)/nrProc;

        //i do the computations on b's columns, however, it is easier to work with rows
        int[][] bT = Utils.transposeMatrix(b, nrRows);


        //send to each process the nr of rows and the step size -> each proc will do row*step number of computations
        int[] aux = new int[2];
        aux[0] = nrRows;
        aux[1] = step;
        for (int i=1;i<nrProc;i++){
            MPI.COMM_WORLD.Issend(aux, 0, 2, MPI.INT, i, 1); //tag 1 for AUX DATA
        }
        for (int i=1;i<nrProc;i++){
            int end = begin+step;
            if (end>nrRows){
                end=nrRows;
            }
            for (int k=begin;k<end;k++){
                int[] row = a[k];
                for (int j=0;j<nrRows;j++){
                    int[] col = bT[j];
                    int [] computed = new int[2];
                    computed[0] = k;
                    computed[1] = j;
                    MPI.COMM_WORLD.Issend(row, 0, nrRows, MPI.INT, i, 0); //TAG 0 for a computeELEM
                    MPI.COMM_WORLD.Issend(col, 0, nrRows, MPI.INT, i, 0);
                    MPI.COMM_WORLD.Issend(computed, 0, 2, MPI.INT, i, 0);
                }
            }
            begin=end;
        }
        //get the results from the workers and add them up
        for (int proc=1;proc<nrProc;proc++){
            int[][] computed = new int[nrRows][nrRows];
            MPI.COMM_WORLD.Recv(computed, 0, nrRows, MPI.OBJECT, proc, 2);
            Utils.addResults(res, computed, nrRows);
        }

        return res;
    }

    private static void addResults(int[][] res, int[][] computed, int n) {
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++) {
                res[i][j] += computed[i][j];
            }
        }
    }

    public static void worker(int rank, Integer nrProc) {
            //receive step and nrRows from parent
            int[] aux = new int[2];
            MPI.COMM_WORLD.Recv(aux, 0, 2, MPI.INT, 0, 1);
            int step = aux[1];
            int n = aux[0];
            int i =0;
            int[][] res = new int[n][n];
            while(i<step*n) {
                int[] row = new int[n];
                int[] col = new int[n];
                MPI.COMM_WORLD.Recv(row, 0, n, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(col, 0, n, MPI.INT, 0, 0);
                int[] k = new int[2];
                MPI.COMM_WORLD.Recv(k, 0, 2, MPI.INT, 0, 0);
                computeOneElement(row, col, n, k[0], k[1], res);
                i++;
            }
            //send results
            MPI.COMM_WORLD.Issend(res, 0, n, MPI.OBJECT, 0, 2); //TAG 2 for send res
    }

}
