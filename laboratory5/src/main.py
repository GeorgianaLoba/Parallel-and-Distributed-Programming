import multiprocessing as mp
import time
from concurrent.futures.thread import ThreadPoolExecutor
import random
from threading import Thread
from typing import List


class Polynomial:
    def __init__(self, n: int, coefficients: List):
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
    def multiplyKaratsubaParallel(args) -> Polynomial:
        depth = args[0]
        A = args[1]
        B = args[2]
        if depth > 4:
            return PolynomialOperations.multiplySequencially(A, B)
        if A.n < 2 or B.n < 2:
            return PolynomialOperations.multiplySequencially(A, B)

        m = int(max(A.n, B.n) / 2)
        lowA = Polynomial(len(A.coefficients[:m]), A.coefficients[:m])
        highA = Polynomial(len(A.coefficients[m:]), A.coefficients[m:])
        lowB = Polynomial(len(B.coefficients[:m]), B.coefficients[:m])
        highB = Polynomial(len(B.coefficients[m:]), B.coefficients[m:])
        karaPool = ThreadPoolExecutor(mp.cpu_count())
        futureResult1 = karaPool.submit(PolynomialOperations.multiplyKaratsubaParallel, ([depth+1, lowA, lowB]))
        futureResult2 = karaPool.submit(PolynomialOperations.multiplyKaratsubaParallel, ([depth+1, PolynomialOperations.add(lowA, highA), PolynomialOperations.add(lowB, highB)]))
        futureResult3 = karaPool.submit(PolynomialOperations.multiplyKaratsubaParallel, ([depth+1, highA, highB]))
        karaPool.shutdown(wait=True)
        result1 = futureResult1.result()
        result2 = futureResult2.result()
        result3 = futureResult3.result()
        r1 = PolynomialOperations.shift(result3, 2 * m)
        r2 = PolynomialOperations.shift(
            PolynomialOperations.subtract(PolynomialOperations.subtract(result2, result3), result1), m)
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

def generate_large_poly_coefficients():
    poly = []
    for _ in range(5000):
        poly.append(random.randint(100000, 1000000))
    return poly

def main():
    A = [5, 0, -10, 6, 8, -3, 15, 22]
    #A = generate_large_poly_coefficients()
    #B = generate_large_poly_coefficients()
    B = [-1, 2, 4, 1, -4, 0, 0, 0, -3, 4, 5]
    m = len(A)
    n = len(B)
    polyA = Polynomial(m, A)
    print(polyA)
    polyB = Polynomial(n, B)
    print(polyB)

    #non-parallel versions

    start1 = time.time()
    polyProduct = PolynomialOperations.multiplySequencially(polyA, polyB)
    print("Sequencial multiplication: " + str(polyProduct))
    end1 = time.time()
    print("Elapsed time: " + str(end1 - start1))

    start2 = time.time()
    polyProductKaratsuba = PolynomialOperations.multiplyKamasutra(polyA, polyB)
    print("Karatsuba multiplication: " + str(polyProductKaratsuba))
    end2 = time.time()
    print("Elapsed time: " + str(end2 - start2))

    start3 = time.time()
    polyProductParallel = PolynomialOperations.multiplySequenciallyParallel(polyA, polyB)
    print("Parallel - Sequencial multiplication: " + str(polyProductParallel))
    end3 = time.time()
    print("Elapsed time: " + str(end3 - start3))

    start4 = time.time()
    polyProduct2 = PolynomialOperations.multiplyKaratsubaParallel([1, polyA, polyB])
    print("Parallel - Karatsuba multiplication: " + str(polyProduct2))
    end4 = time.time()
    print("Elapsed time: " + str(end4 - start4))

if __name__ == '__main__':
    main()
