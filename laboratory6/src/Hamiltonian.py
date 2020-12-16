from copy import deepcopy
from threading import Thread


class Hamiltonian:
    def __init__(self, graph):
        self.__graph = graph
        self.__hamiltonian=[]
        self.__hasSolution=False

    def hamiltonCircuit(self):
        path = [-1 for i in range(self.__graph.getNumberVertices())]
        path[0] = 0
        if self.solveHamiltonCircuit(path, 1):
            self.__hasSolution = True
            self.__hamiltonian = deepcopy(path)

    def backtracking(self, solution: [int], k: int, numberVertices: int) -> None:
        sol, path = self.isSolution(solution, numberVertices)
        if sol:
            self.__hasSolution = True
            self.__hamiltonian = deepcopy(path)
        else:
            v = solution[-1]
            neighbours = self.__graph.neighbours(v)
            for node in neighbours:
                if self.__graph.isEdge(solution[k], node) and not self.alreadyVisited(solution, node):
                    solution.append(node)
                    self.backtracking(solution, k + 1, numberVertices)
                    solution.pop()

    def threadedBacktracking(self, solution: [int], k: int, numberVertices:int):
        sol, path = self.isSolution(solution, numberVertices)
        if sol:
            self.__hasSolution = True
            self.__hamiltonian = deepcopy(path)
        else:
            v = solution[k]
            neighbours = self.__graph.neighbours(v)
            for node in neighbours:
                if self.__graph.isEdge(solution[k], node) and not self.alreadyVisited(solution, node):
                    solution.append(node)
                    thread = Thread(target=self.backtracking(solution, k+1, numberVertices))
                    # thread.start()
                    # solution.pop()
                    # thread.join()
                    init = False
                    if k < 2:
                        self.threadedBacktracking(solution, k + 1, numberVertices)
                        solution.pop()
                    else:
                        thread.start()
                        init = True
                    if (init):
                        solution.pop()
                        thread.join()

    def hasSolution(self):
        return self.__hasSolution

    def getHamiltonian(self):
        return self.__hamiltonian

    def isSolution(self, solution: [int], n:int):
        path = []
        for node in solution:
            if node != -1:
                path.append(node)
        if len(path) == self.__graph.getNumberVertices() and self.__graph.isEdge(path[-1], path[0]):
            return True, path
        return False, []

    def alreadyVisited(self, solution: [int], node: int) -> bool:
        if node in solution:
            return True
        return False

    def solveHamiltonCircuit(self, path, pos):
        if pos == self.__graph.getNumberVertices():
            if self.__graph.isEdge(path[-1], path[0]):
                return True
            else:
                return False
        for v in range(1, self.__graph.getNumberVertices()):
            if self.canBeAdded(path, pos, v):
                path[pos] = v
                if self.solveHamiltonCircuit(path, pos+1):
                    return True
                path[pos] = -1
        return False
    #
    # def solveHamiltonCircuitThread(self, path, pos):
    #     if pos == self.__graph.getNumberVertices():
    #         if self.__graph.isEdge(path[-1], path[0]):
    #             return True
    #         else:
    #             return False
    #     for v in range(1, self.__graph.getNumberVertices()):
    #         if self.canBeAdded(path, pos, v):
    #             path[pos] = v
    #             thread = Thread(target=self.solveHamiltonCircuitThread(path, pos + 1))
    #             init = False
    #             if (pos < 10):
    #                 self.threadedBacktracking(path, pos + 1)
    #                 solution.pop()
    #             else:
    #                 thread.start()
    #                 init = True
    #             if init:
    #                 thread.join()
    #             if self.solveHamiltonCircuit(path, pos + 1):
    #                 return True
    #             path[pos] = -1
    #     return False

    def canBeAdded(self, path, pos, v):
        if self.alreadyVisited(path, v):
            return False
        if self.__graph.isEdge(path[pos-1], v):
            return True
        return True
