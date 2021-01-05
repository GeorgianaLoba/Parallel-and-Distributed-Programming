using System;
using System.Collections.Generic;
using System.Text;

namespace mpiFIRST
{
    [Serializable]
    public class Polynomial
    {
        public int N { get; set; }
        public int[] Coefficients { get; set; }
        
        public Polynomial(int n)
        {
            N = n;
            Coefficients = new int[N];
        }

        public Polynomial(int n, int[] coefficients)
        {
            N = n;
            Coefficients = coefficients;
        }

        public void GeneratePolynomial()
        {
            Random random = new Random();
            for (int i = 0; i < N; i++)
            {
                Coefficients[i] = random.Next(-10, 10);
            }
        }
        
        public Polynomial Sum(Polynomial B)
        {
            int DegreeA = N;
            int DegreeB = B.N;
            int DegreeSum;
            if (DegreeA > DegreeB)
            {
                DegreeSum = DegreeA;
                int[] CoefficientsSum =new int[DegreeSum];
                for (int i = 0; i < DegreeB; i++)
                {
                    CoefficientsSum[i] = Coefficients[i] + B.Coefficients[i];
                }
                for (int i = DegreeB; i < DegreeA; i++)
                {
                    CoefficientsSum[i] = Coefficients[i];
                }
                return new Polynomial(DegreeSum, CoefficientsSum);
            }
            else
            {
                DegreeSum = DegreeB;
                int[] CoefficientsSum = new int[DegreeSum];
                for (int i = 0; i < DegreeA; i++)
                {
                    CoefficientsSum[i] = Coefficients[i] + B.Coefficients[i];
                }
                for (int i = DegreeA; i < DegreeB; i++)
                {
                    CoefficientsSum[i] = B.Coefficients[i];
                }
                return new Polynomial(DegreeSum, CoefficientsSum);
            }
            
        }

        public Polynomial Difference(Polynomial B)
        {
            int DegreeA = N;
            int DegreeB = B.N;
            int DegreeSum;
            if (DegreeA > DegreeB)
            {
                DegreeSum = DegreeA;
                int[] CoefficientsSum = new int[DegreeSum];
                for (int i = 0; i < DegreeB; i++)
                {
                    CoefficientsSum[i] = Coefficients[i] - B.Coefficients[i];
                }
                for (int i = DegreeB; i < DegreeA; i++)
                {
                    CoefficientsSum[i] = Coefficients[i];
                }
                return new Polynomial(DegreeSum, CoefficientsSum);
            }
            else
            {
                DegreeSum = DegreeB;
                int[] CoefficientsSum = new int[DegreeSum];
                for (int i = 0; i < DegreeA; i++)
                {
                    CoefficientsSum[i] = Coefficients[i] - B.Coefficients[i];
                }
                for (int i = DegreeA; i < DegreeB; i++)
                {
                    CoefficientsSum[i] = -1 * B.Coefficients[i];
                }
                return new Polynomial(DegreeSum, CoefficientsSum);
            }

        }

        public Polynomial GetFirstMCoefficients(int m)
        {
            Polynomial result = new Polynomial(m);
            for (int i = 0; i < m; i++)
            {
                result.Coefficients[i] = Coefficients[i];
            }
            return result;
        }

        public Polynomial GetLastMCoefficients(int m)
        {
            Polynomial result = new Polynomial(m);
            int k = 0;
            for (int i = N-m; i < N; i++)
            {
                result.Coefficients[k] = Coefficients[i];
                k++;
            }
            return result;
        }

        public Polynomial Shift(int m)
        {
            int[] newCoefficients = new int[N + m];
            int newSize = N + m;

            for (int i = m; i < N+m; i++)
            {
                newCoefficients[i] = Coefficients[i-m];
            }
            for (int i = 0; i < m; i++)
            {
                newCoefficients[i] = 0;
            }
            return new Polynomial(newSize, newCoefficients);
        }


        public override string ToString()
        {
            string str = "";
            for (int i = 0; i < N; i++)
            {
                if (i == 0)
                {
                    str += Coefficients[i];
                }
                else
                {
                    str += " + ";
                    str += Coefficients[i] + "x^" + i;
                }
            }
            return str;
        }

    }
}
