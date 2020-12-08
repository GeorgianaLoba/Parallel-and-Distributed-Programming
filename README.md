# Parallel-and-Distributed-Programming

  [Lab 1 - "Non-cooperative" multithreading](#first-lab) </br>
  [Lab 2 - Producer-consumer synchronization](#second-lab)  </br>
  [Lab 3 - Simple parallel tasks](#third-lab) </br>
  [Lab 4 - Futures and continuations](#fourth-lab) </br>
  [Lab 5 - Parallelizing techniques](#fifth-lab) </br>


### First Lab

<b> Laboratory 1 requirements: </b>

   The problems will require to execute a number of independent operations, that operate on shared data.
   There shall be several threads launched at the beginning, and each thread shall execute a lot of operations. The operations to be executed are to be randomly choosen, and with randomly choosen parameters.
   The main thread shall wait for all other threads to end and, then, it shall check that the invariants are obeyed.
   The operations must be synchronized in order to operate correctly. Write, in a documentation, the rules (which mutex what invariants it protects).
   You shall play with the number of threads and with the granularity of the locking, in order to asses the performance issues. Document what tests have you done, on what hardware platform, for what size of the data, and what was the time consumed.

Bank accounts

At a bank, we have to keep track of the balance of some accounts. Also, each account has an associated log (the list of records of operations performed on that account). Each operation record shall have a unique serial number, that is incremented for each operation performed in the bank.

We have concurrently run transfer operations, to be executer on multiple threads. Each operation transfers a given amount of money from one account to someother account, and also appends the information about the transfer to the logs of both accounts.

From time to time, as well as at the end of the program, a consistency check shall be executed. It shall verify that the amount of money in each account corresponds with the operations records associated to that account, and also that all operations on each account appear also in the logs of the source or destination of the transfer.

### Second Lab

<b> Laboratory 2 requirements: </b>

Create two threads, a producer and a consumer, with the producer feeding the consumer.

Requirement: Compute the scalar product of two vectors.

Create two threads. The first thread (producer) will compute the products of pairs of elements - one from each vector - and will feed the second thread. The second thread (consumer) will sum up the products computed by the first one. The two threads will behind synchronized with a condition variable and a mutex. The consumer will be cleared to use each product as soon as it is computed by the producer thread.

### Third Lab

<b>Laboratory 3 requirements:</b>


Write several programs to compute the product of two matrices.

Have a function that computes a single element of the resulting matrix.

Have a second function whose each call will constitute a parallel task (that is, this function will be called on several threads in parallel). This function will call the above one several times consecutively to compute several elements of the resulting matrix. Consider the following ways of splitting the work betweeb tasks (for the examples, consider the final matrix being 9x9 and the work split into 4 tasks):

   Each task computes consecutive elements, going row after row. So, task 0 computes rows 0 and 1, plus elements 0-1 of row 2 (20 elements in total); task 1 computes the remainder of row 2, row 3, and elements 0-3 of row 4 (20 elements); task 2 computes the remainder of row 4, row 5, and elements 0-5 of row 6 (20 elements); finally, task 3 computes the remaining elements (21 elements).
   
   Each task computes consecutive elements, going column after column. This is like the previous example, but interchanging the rows with the columns: task 0 takes columns 0 and 1, plus elements 0 and 1 from column 2, and so on.
    
   Each task takes every k-th element (where k is the number of tasks), going row by row. So, task 0 takes elements (0,0), (0,4), (0,8), (1,3), (1,7), (2,2), (2,6), (3,1), (3,5), (4,0), etc.

For running the tasks, also implement 2 approaches:

   Create an actual thread for each task (use the low-level thread mechanism from the programming language)
   
   Use a thread pool.


### Fourth Lab

<b> Laboratory 4 requirements: </b>

The goal of this lab is to use C# TPL futures and continuations in a more complex scenario, in conjunction with waiting for external events.

Write a program that is capable of simultaneously downloading several files through HTTP. Use directly the BeginConnect()/EndConnect(), BeginSend()/EndSend() and BeginReceive()/EndReceive() Socket functions, and write a simple parser for the HTTP protocol (it should be able only to get the header lines and to understand the Content-lenght: header line).

Try three implementations:

 <ul>
 <li>Directly implement the parser on the callbacks (event-driven); </li>
 <li>Wrap the connect/send/receive operations in tasks, with the callback setting the result of the task; </li>
 <li>Like the previous, but also use the async/await mechanism. </li> </ul>
 
 ### Fifth Lab

 <b> Laboratory 5 requirements: </b>
  
The goal of this lab is to implement a simple but non-trivial parallel algorithm.

Perform the multiplication of 2 polynomials. Use both the regular O(n2) algorithm and the Karatsuba algorithm, and each in both the sequencial form and a parallelized form. Compare the 4 variants.

The documentation will describe:
<ul> 
    <li>the algorithms </li>
    <li>the synchronization used in the parallelized variants</li>
    <li>the performance measurements </li>
</ul>
