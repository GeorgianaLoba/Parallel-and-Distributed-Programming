import time

from src.Graph import Graph
from src.Hamiltonian import Hamiltonian


def main():
    graph10 = Graph('graph.in')

    hamiltonian10 = Hamiltonian(graph10)
    start10 = time.time()
    hamiltonian10.backtracking([0], 0, graph10.getNumberVertices())
    if hamiltonian10.hasSolution():
        print(hamiltonian10.getHamiltonian())
    else:
        print('No hamiltonian path!')
    end10 = time.time()
    print("for small graph: Elapsed time: " + str(end10 - start10))

    hamiltonian20 = Hamiltonian(graph10)
    start20 = time.time()
    hamiltonian20.threadedBacktracking([0], 0, graph10.getNumberVertices())
    if hamiltonian20.hasSolution():
        print(hamiltonian20.getHamiltonian())
    else:
        print('No hamiltonian path!')
    end20 = time.time()
    print("for small graph parallelled: Elapsed time: " + str(end20 - start20))

    graph = Graph('graph2.in')
    hamiltonian = Hamiltonian(graph)
    start1 = time.time()
    hamiltonian.backtracking([0], 0, graph.getNumberVertices())
    if hamiltonian.hasSolution():
        print(hamiltonian.getHamiltonian())
    else:
        print('No hamiltonian path!')
    end1 = time.time()
    print("for larger graph: Elapsed time: " + str(end1 - start1))

    graph2 = Graph('graph2.in')
    hamiltonian2 = Hamiltonian(graph2)
    start2 = time.time()
    hamiltonian2.threadedBacktracking([0], 0, graph2.getNumberVertices())
    if hamiltonian2.hasSolution():
        print(hamiltonian2.getHamiltonian())
    else:
        print('No hamiltonian path!')
    end2 = time.time()
    print("for larger graph parallelled: Elapsed time: " + str(end2 - start2))


    graph4 = Graph('graph1.hcp')
    hamiltonian4 = Hamiltonian(graph4)
    start4 = time.time()
    hamiltonian4.threadedBacktracking([1], 0, graph4.getNumberVertices())
    if hamiltonian4.hasSolution():
        print(hamiltonian4.getHamiltonian())
    else:
        print('No hamiltonian path!')
    end4 = time.time()
    print("large acyclic graph: Elapsed time: " + str(end4 - start4))

    graph5 = Graph('graph1.hcp')
    hamiltonian5 = Hamiltonian(graph5)
    start5 = time.time()
    hamiltonian5.threadedBacktracking([1], 0, graph5.getNumberVertices())
    if hamiltonian5.hasSolution():
        print(hamiltonian5.getHamiltonian())
    else:
        print('No hamiltonian path!')
    end5 = time.time()
    print("large acyclic graph parallelled: Elapsed time: " + str(end5 - start5))


if __name__ == '__main__':
    main()
