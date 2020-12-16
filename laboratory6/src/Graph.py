class Graph:
    def __init__(self, fileName):
        """
        Directed graph represented using a dictionary with key=(int, int) and val = (0 or 1)
        #(i,j)=1 if there exists an edge between i and j, direction i->j
        :param fileName: string
        """
        self.__fileName = fileName
        self.__graph = {}
        self.__numberVertices = None
        self.__numberEdges = None
        self.__readFile()

    def __readFile(self):
        """
        The file structure should have the following pattern:
            first line: nrOfVertices = int
            second line: nrOfEdges = int
            starting from third line: edges = int int
        """
        with open(self.__fileName, 'r') as file:
            self.__numberVertices = int(file.readline())
            self.__numberEdges = int(file.readline())
            for line in file:
                line.strip()
                splitter = line.split()
                self.__graph[(int(splitter[0]), int(splitter[1]))] = 1
        # for i in range(self.__numberVertices):
        #     for j in range(self.__numberVertices):
        #         if not (i, j) in self.__graph.keys():
        #             self.__graph[(i,j)] = 0

    def isEdge(self, i: int, j: int) -> bool:
        # if self.__graph[(i,j)] == 1:
        #     return True
        return (i, j) in self.__graph.keys()

    def neighbours(self, vertex: int) -> [int]:
        edges = []
        for edge in self.__graph.keys():
            if edge[0] == vertex:
                edges.append(edge[1])
        return edges

    def getGraph(self):
        return self.__graph

    def getFileName(self):
        return self.__fileName

    def getNumberVertices(self):
        return self.__numberVertices

    def getNumberEdges(self):
        return self.__numberEdges

    def __str__(self) -> str:
        bd = "Graph with "
        bd += str(self.__numberVertices) + " vertices and "
        bd += str(self.__numberEdges) + " edges. \n"
        bd += "It has the following edges: \n"
        for key in self.__graph.keys():
            # if self.isEdge(key[0], key[1]):
            bd += str(key[0]) + "->" + str(key[1]) +"\n"
        return bd
