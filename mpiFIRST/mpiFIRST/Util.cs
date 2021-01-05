using System;
using System.Collections.Generic;
using System.Text;

namespace mpiFIRST
{
    class Util
    {
        public static Polynomial Multiplication(Polynomial A, Polynomial B)
        {
            int DegreeMul = A.N + B.N - 1;
            Polynomial polyMul = new Polynomial(DegreeMul);
            for (int i = 0; i < A.N; i++)
            {
                for (int j = 0; j < B.N; j++)
                {
                    polyMul.Coefficients[i + j] += A.Coefficients[i] * B.Coefficients[j];
                }
            }
            return polyMul;
        }
        public static Polynomial MpiMultiplication(Polynomial A, Polynomial B, int start, int end)
        {
            Console.WriteLine(start +"->" + end);
            int DegreeMul = A.N + B.N - 1;
            Polynomial polyMul = new Polynomial(DegreeMul);
            for (int i = start; i < end; i++)
            {
                for (int j = 0; j < B.N; j++)
                {
                    polyMul.Coefficients[i + j] += A.Coefficients[i] * B.Coefficients[j];
                }
            }
            return polyMul;
        }

        public static Polynomial Karatsuba(Polynomial A, Polynomial B)
        {
            if (A.N < 2 || B.N < 2)
            {
                return Util.Multiplication(A, B);
            }
            int m = Math.Min(A.N, B.N) / 2;
            Polynomial lowA = A.GetFirstMCoefficients(m);
            Polynomial highA = A.GetLastMCoefficients(A.N-m);
            Polynomial lowB = B.GetFirstMCoefficients(m);
            Polynomial highB = B.GetLastMCoefficients(B.N-m);

            Polynomial z1 = Karatsuba(lowA, lowB);
            Polynomial z2 = Karatsuba(lowA.Sum(highA), lowB.Sum(highB));
            Polynomial z3 = Karatsuba(highA, highB);

            Polynomial result1 = z3.Shift(2 * m);
            Polynomial result2 = z2.Difference(z3).Difference(z1).Shift(m);
            Polynomial result3 = result1.Sum(result2).Sum(z1);

            return result3;
        }
    }
}
