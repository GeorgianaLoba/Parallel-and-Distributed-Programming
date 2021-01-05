using System;
using MPI;

namespace mpiFIRST
{

    class Program
    {

        public static void MpiParent(Polynomial A, Polynomial B)
        {
            int processes = Communicator.world.Size;
            int start;
            int end = 0;
            int length = A.N / (processes-1) ;
            for (int i = 1; i < processes; i++) //send to children
            {
                start = end;
                end += length;
                if (i == processes - 1)
                    end = A.N;
                Communicator.world.Send(A, i, 0);
                Communicator.world.Send(B, i, 0);
                Communicator.world.Send(start, i, 0);
                Communicator.world.Send(end, i, 0);
            }
            Polynomial[] resulting = new Polynomial[processes];
            for (int i = 1; i < processes; i++)
            {
                resulting[i] = Communicator.world.Receive<Polynomial>(i, 0);
            }
            Polynomial result = new Polynomial(resulting[1].N);
            for (int i = 0; i < result.N; i++)
                for (int j = 1; j < resulting.Length; j++)
                    result.Coefficients[i] += resulting[j].Coefficients[i];
            Console.WriteLine("MPI MULTI: " + result.ToString());
        }
        
        public static void MpiChild()
        {
            Polynomial polynomial1 = Communicator.world.Receive<Polynomial>(0, 0);
            Polynomial polynomial2 = Communicator.world.Receive<Polynomial>(0, 0);
            int start = Communicator.world.Receive<int>(0, 0);
            int end = Communicator.world.Receive<int>(0, 0);

            Polynomial result = Util.MpiMultiplication(polynomial1, polynomial2, start, end);
            Communicator.world.Send(result, 0, 0);
        }


        public static void MpiKaratsubaParent(Polynomial A, Polynomial B)
        {
            int nrProc = MPI.Communicator.world.Size;
            int end = 0;
            if (nrProc <= 1)
            {
                Polynomial resulted = Util.Karatsuba(A, B);
                Console.WriteLine(resulted);
                return;
            }
            int len = A.N / (nrProc - 1);

            for (int i = 1; i < nrProc; i++)
            {
                int begin = end;
                end += len;
                if (i == nrProc - 1)
                {
                    end = A.N;
                }
                MPI.Communicator.world.Send(A, i, 10);
                MPI.Communicator.world.Send(B, i, 11);
                MPI.Communicator.world.Send(begin, i, 12);
                MPI.Communicator.world.Send(end, i, 13);
            }
            Console.WriteLine("result expected...");
            Polynomial[] resulting = new Polynomial[nrProc];
            for (int i = 1; i < nrProc; i++)
            {
                resulting[i] = Communicator.world.Receive<Polynomial>(i, 15);
            }

            Polynomial result = new Polynomial(resulting[1].N);
            for (int i = 0; i < result.N; i++)
                for (int j = 1; j < resulting.Length; j++)
                    result.Coefficients[i] += resulting[j].Coefficients[i];
            Console.WriteLine("MPI MULTI KARATSUBA: " + result.ToString());
        }

        public static void MpiKaratsubaChild()
        {
            Polynomial A = MPI.Communicator.world.Receive<Polynomial>(0, 10);
            Polynomial B = MPI.Communicator.world.Receive<Polynomial>(0, 11);
            int start = MPI.Communicator.world.Receive<int>(0, 12);
            int end = MPI.Communicator.world.Receive<int>(0, 13);
            for (int i = 0; i < start; i++)
            {
                A.Coefficients[i] = 0;
            }

            for (int i = end; i < A.N; i++)
            {
                A.Coefficients[i] = 0;
            }
  
            Polynomial result = Util.Karatsuba(A, B);
            Console.WriteLine("result send...");

            MPI.Communicator.world.Send(result, 0, 15);
        }


        static void Main(string[] args)
        {

            using (new MPI.Environment(ref args))
            {
                Intracommunicator world = Communicator.world;
                if (world.Rank == 0)
                {
                    Polynomial polyA = new Polynomial(4);
                    polyA.GeneratePolynomial();
                    Polynomial polyB = new Polynomial(8);
                    polyB.GeneratePolynomial();

                    Console.WriteLine("A: " + polyA.ToString());
                    Console.WriteLine("B: " + polyB.ToString());


                    //world.Send("trimit la alt proces", 1, 0);
                    //string message = world.Receive<string>(Communicator.anySource, 0);
                    Console.WriteLine("Rank: " + world.Rank);
                    //Console.WriteLine("PolyMULCheck: " + Util.Multiplication(polyA,polyB).ToString());
                    Console.WriteLine("PolyKaraCheck: " + Util.Karatsuba(polyA,polyB).ToString());
                    //MpiParent(polyA, polyB);
                    MpiKaratsubaParent(polyA, polyB);

                }
                else
                {
                    //string message = world.Receive<string>(world.Rank - 1, 0);
                    //Console.WriteLine("Rank: " + world.Rank + " received message: \"" + message + "\" ");
                    //world.Send(message + "," + world.Rank, (world.Rank + 1) % world.Size, 0);
                    Console.WriteLine("Rank: " + world.Rank);
                    //MpiChild();
                    MpiKaratsubaChild();
                }

            }
        }
    }

}
