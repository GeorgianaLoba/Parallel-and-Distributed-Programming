# A[] represents coefficients of first polynomial
# B[] represents coefficients of second polynomial
# m and n are sizes of A[] and B[] respectively
import multiprocessing as mp
from threading import Thread
from typing import List


class Polynomial:
    def __init__(self, n: int, coefficients: List[int]):
        self.n = n
        self.coefficients = coefficients

    def __str__(self) -> str:
        builder = ""
        for i in range(self.n):
            builder += str(self.coefficients[i]) + ""
            if (i != 0):
                builder += "x^" + str(i)
            if (i != self.n - 1):
                builder += " + "
        return builder


class PolynomialOperations:
    @staticmethod
    def add(A: Polynomial, B: Polynomial) -> Polynomial:
        if A.n > B.n:
            size = A.n
            sumList = [0] * size
            for i in range(B.n):
                sumList[i] = A.coefficients[i] + B.coefficients[i]
            for i in range(B.n, A.n, 1):
                sumList[i] = A.coefficients[i]
            return Polynomial(size, sumList)
        else:
            size = B.n
            sumList = [0] * size
            for i in range(A.n):
                sumList[i] = A.coefficients[i] + B.coefficients[i]
            for i in range(A.n, B.n, 1):
                sumList[i] = B.coefficients[i]
            return Polynomial(size, sumList)

    @staticmethod
    def subtract(A: Polynomial, B: Polynomial) -> Polynomial:
        if A.n > B.n:
            size = A.n
            sumList = [0] * size
            for i in range(B.n):
                sumList[i] = A.coefficients[i] - B.coefficients[i]
            for i in range(B.n, A.n, 1):
                sumList[i] = A.coefficients[i]
            return Polynomial(size, sumList)
        else:
            size = B.n
            sumList = [0] * size
            for i in range(A.n):
                sumList[i] = A.coefficients[i] - B.coefficients[i]
            for i in range(A.n, B.n, 1):
                sumList[i] = - B.coefficients[i]
            return Polynomial(size, sumList)


    @staticmethod
    def shift(A: Polynomial, by: int):
        coeff = []
        for i in range(by):
            coeff.append(0)
        for i in range(len(A.coefficients)):
            coeff.append(A.coefficients[i])
        return Polynomial(A.n+by, coeff)


    @staticmethod
    def multiplySequencially(A: Polynomial, B: Polynomial) -> Polynomial:
        size = A.n + B.n - 1
        productList = [0] * size
        for i in range(A.n):
            for j in range(B.n):
                productList[i + j] += A.coefficients[i] * B.coefficients[j]
        return Polynomial(size, productList)

    @staticmethod
    def multiplyKamasutra(A: Polynomial, B: Polynomial) -> Polynomial:
        if A.n < 2 or B.n < 2:
            return PolynomialOperations.multiplySequencially(A,B)
        m = int(max(A.n, B.n) / 2)
        lowA = Polynomial(len(A.coefficients[:m]), A.coefficients[:m])
        highA = Polynomial(len(A.coefficients[m:]), A.coefficients[m:])
        lowB = Polynomial(len(B.coefficients[:m]), B.coefficients[:m])
        highB = Polynomial(len(B.coefficients[m:]), B.coefficients[m:])

        result1 = PolynomialOperations.multiplyKamasutra(lowA, lowB)
        result2 = PolynomialOperations.multiplyKamasutra(PolynomialOperations.add(lowA, highA), PolynomialOperations.add(lowB, highB))
        result3 = PolynomialOperations.multiplyKamasutra(highA, highB)

        r1 = PolynomialOperations.shift(result3, 2*m)
        r2 = PolynomialOperations.shift(PolynomialOperations.subtract(PolynomialOperations.subtract(result2, result3), result1), m)
        return PolynomialOperations.add(PolynomialOperations.add(r1, r2), result1)

    @staticmethod
    def do_multiplication(A: Polynomial, B: Polynomial, product: List[int], start: int, end: int) -> None:
        for i in range(start, end, 1):
            if i > len(product):
                return
            j = 0
            while j <= i:
                if j < len(A.coefficients) and (i - j) < len(B.coefficients):
                    product[i] += A.coefficients[j] * B.coefficients[i - j]
                j = j + 1

    @staticmethod
    def multiplySequenciallyParallel(A: Polynomial, B: Polynomial) -> Polynomial:
        size = A.n + B.n - 1
        product = [0] * size
        threads = []
        nrThreads = mp.cpu_count()
        nr = int(size / nrThreads)
        if nr == 0:
            nr = 1
        i = 0
        while i < nrThreads:
            j = i + nr
            threads.append(Thread(target=PolynomialOperations.do_multiplication, args=(A, B, product, i, j)))
            i += nr
        for thread in threads:
            thread.start()
        for thread in threads:
            thread.join()
        return Polynomial(size, product)


def main():
    A = [5, 0, 10, 6]
    B = [1, 2, 4]
    m = len(A)
    n = len(B)
    polyA = Polynomial(m, A)
    print(polyA)
    polyB = Polynomial(n, B)
    print(polyB)
    polyProduct = PolynomialOperations.multiplySequencially(polyA, polyB)
    print(polyProduct)
    polyProductKamasutra = PolynomialOperations.multiplyKamasutra(polyA, polyB)
    print(polyProductKamasutra)
    # polyProductParallel = PolynomialOperations.multiplySequenciallyParallel(polyA, polyB)
    # print(polyProductParallel)
    #print(PolynomialOperations.shift(polyA, 2))

if __name__ == '__main__':
    main()
