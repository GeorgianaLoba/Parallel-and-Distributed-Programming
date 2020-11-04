import concurrent
import copy
import random
import threading
from concurrent.futures.thread import ThreadPoolExecutor
from threading import Thread

NUMBER_OF_THREADS = 4
NUMBER_OF_TASKS = 3
M1_ROWS = 4
M1_COLS = 3
M2_ROWS = 3
M2_COLS = 5
TYPE = 3


def init():
    # (4,3)
    matrix1 = [[random.randint(0, 50) for col in range(M1_COLS)] for row in range(M1_ROWS)]
    # (3,5)
    matrix2 = [[random.randint(0, 50) for col in range(M2_COLS)] for row in range(M2_ROWS)]
    # (4, 5)
    result = [[0 for y in matrix2[0]] for x in matrix1]
    return matrix1, matrix2, result


def split_work():
    return M1_ROWS*M2_COLS, int(M1_ROWS*M2_COLS / NUMBER_OF_TASKS)


def compute_element_for_result(m1, m2, i, j):
    s = 0
    for k in range(len(m1[i])):
        s += m1[i][k] * m2[k][j]
    return s


def do_assigned_task(m1, m2, result, specifics):
    for tpl in specifics:
        result[tpl[0]][tpl[1]] = compute_element_for_result(m1, m2, tpl[0], tpl[1])
    return True


def get_specifics_1(counter):
    specifics = []
    specific, idx = [], 0
    for i in range(M1_ROWS):
        for j in range(M2_COLS):
            if idx == counter:
                specifics.append(copy.deepcopy(specific[:]))
                specific.clear()
                idx = 0
            idx += 1
            specific.append((i, j))
    if len(specific) < counter:
        specifics[-1] += specific
    else:
        specifics.append(specific)
    return specifics


def get_specifics_2(counter):
    specifics = []
    specific, idx = [], 0
    for j in range(M2_COLS):
        for i in range(M1_ROWS):
            if idx == counter:
                specifics.append(copy.deepcopy(specific[:]))
                specific.clear()
                idx = 0
            idx += 1
            specific.append((i,j))
    if len(specific) < counter:
        specifics[-1] += specific
    return specifics


def get_specifics_3(counter, k):
    specifics = []
    specific, i, current, idx,f = [], 0, 0, 0,0
    specific.append((i, current))
    idx += 1
    f += 1
    while True:
        current += k
        if current < M2_COLS:
            if (i >= M1_ROWS):
                i = 0
            specific.append((i, current))
            idx += 1
            f += 1
        else:
            current=current-M2_COLS
            i += 1
            idx += 1
            f += 1
            if (i >= M1_ROWS):
                i = 0
            specific.append((i, current))
        if idx == counter:
            specifics.append(copy.deepcopy(specific[:]))
            specific.clear()
            idx = 0
        if f == M1_ROWS * M2_COLS:
            break
    if len(specific) < counter:
        specifics[-1] += specific
    return specifics


def run_regular_threads(m1, m2, result):
    result_size, counter = split_work()
    specifics=[]
    if TYPE == 1:
        specifics = get_specifics_1(counter)
    if TYPE == 2:
        specifics = get_specifics_2(counter)
    if TYPE == 3:
        specifics = get_specifics_3(counter, NUMBER_OF_TASKS)
    threads = []
    for i in range(NUMBER_OF_TASKS):
        threads.append(Thread(target=do_assigned_task, args=(m1, m2, result, specifics[i])))
    for thread in threads:
        thread.start()
    for thread in threads:
        thread.join()


def run_pool_threads(m1, m2, result):
    result_size, counter = split_work()
    specifics=[]
    if TYPE == 1:
        specifics = get_specifics_1(counter)
    if TYPE == 2:
        specifics = get_specifics_2(counter)
    if TYPE == 3:
        specifics = get_specifics_3(counter, NUMBER_OF_TASKS)
    with ThreadPoolExecutor(NUMBER_OF_THREADS) as executor:
        res = {executor.submit(do_assigned_task, m1, m2, result, specific): specific for specific in specifics}
        for future in concurrent.futures.as_completed(res):
            stff = res[future]
        try:
            data = future.result()
        except Exception as exc:
            print('oopsie' + str(exc))


def run_sequential_check(m1, m2):
    result = [[0 for y in m2[0]] for x in m1]
    for i in range(len(m1)):
        for j in range(len(m2[0])):
            for k in range(len(m2)):
                result[i][j] += m1[i][k] * m2[k][j]
    return result


def main():
    # TODO check if okay no global
    m1, m2, result = init()
    run_regular_threads(m1, m2, result)
    #run_pool_threads(m1, m2, result)
    print(m1)
    print(m2)
    result2 = run_sequential_check(m1, m2)
    print(result)
    print(result2)

if __name__ == '__main__':
    main()
