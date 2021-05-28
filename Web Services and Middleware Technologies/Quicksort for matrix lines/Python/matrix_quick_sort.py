import random
import sys

matrix = []


def read_from_file(filename):
    rows = 0
    columns = 0
    strings = []
    with open(filename, 'r') as f:
        while True:
            line = f.readline()
            if not line:
                break
            s = line.split(',')
            if len(s) != 3:
                print("Incorrect line")
                continue
            if int(s[0]) > rows:
                rows = int(s[0])
            if int(s[1]) > columns:
                columns = int(s[1])
            strings.append(line)

    rows += 1
    columns += 1
    for i in range(0, rows):
        line = [None] * columns
        matrix.append(line)

    for s in strings:
        strs = s.split(',')
        l = int(strs[0])
        c = int(strs[1])
        matrix[l][c] = strs[2].strip()


def print_matrix():
    for i in range(len(matrix)):
        for j in range(len(matrix[i])):
            print(matrix[i][j], end=' ')
        print()
    print()


def randomized_quicksort3(up, down):
    if up >= down:
        return

    k = up + random.randint(up, sys.maxsize) % (down - up)
    matrix[k], matrix[up] = matrix[up][:], matrix[k][:]
    positions = partition3(up, down)

    randomized_quicksort3(up, positions[0] - 1)
    randomized_quicksort3(positions[1] + 1, down)


def partition3(up, down):
    x = matrix[up][:]
    j = up
    for i in range(up + 1, down + 1):
        comp = compare_lines(matrix[i], x)
        if comp is None:
            continue
        if comp is True:
            matrix[j], matrix[i] = matrix[i][:], matrix[j][:]
            j += 1
        else:
            matrix[i], matrix[down] = matrix[down][:], matrix[i][:]
            down -= 1
            i -= 1
    return [j, down]


def compare_lines(l1, l2):
    for i in range(0, len(l1)):
        if l1[i] == l2[i]:
            continue
        if l1[i] is None:
            return True
        if l2[i] is None:
            return False
        return l1[i] < l2[i]
    return None


if __name__ == '__main__':
    filename = 'files\\'
    if len(sys.argv) == 1:
        filename += input('Read the file name: ')
    else:
        filename += sys.argv[1]
    read_from_file(filename)
    print('Initial matrix')
    print_matrix()
    randomized_quicksort3(0, len(matrix) - 1)
    print('Sorted matrix')
    print_matrix()
