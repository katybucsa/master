const readline = require("readline");
const prompt = require('prompt-sync')();
const fs = require('fs');

let matrix = [];

function readFromFile(filename) {

    let rows = 0;
    let columns = 0;
    let strings = [];
    const data = (fs.readFileSync(filename, 'utf8')).trim().split(/\r?\n/);
    for (const line of data) {
        let s = line.trim().split(',');
        if (s.length !== 3) {
            console.log('Incorrect line');
        } else {
            if (parseInt(s[0]) > rows)
                rows = parseInt(s[0]);
            if (parseInt(s[1]) > columns)
                columns = parseInt(s[1]);
            strings.push(line);
        }
    }
    rows++;
    columns++;
    for (let i = 0; i < rows; i++) {
        let line = new Array(columns).fill(null);
        matrix.push(line);
    }

    for (let s of strings) {
        let strs = s.split(',');
        let l = parseInt(strs[0]);
        let c = parseInt(strs[1]);
        matrix[l][c] = strs[2];
    }
}


function printMatrix() {

    console.log(matrix);
}


function randomizedQuickSort3(up, down) {

    if (up >= down)
        return;

    let k = up + Math.floor(Math.random() * 1000) % (down - up + 1);
    let temp = matrix[k];
    matrix[k] = matrix[up];
    matrix[up] = temp;
    const positions = partition3(up, down);

    randomizedQuickSort3(up, positions[0] - 1);
    randomizedQuickSort3(positions[1] + 1, down);
}

function partition3(up, down) {

    const x = matrix[up];
    let j = up;
    for (let i = up + 1; i <= down; i++) {
        // console.log(up);
        // console.log(matrix[0]);
        // console.log(x);
        const comp = compareLines(matrix[i], x);
        if (null === comp) continue;
        if (comp) {
            const temp = matrix[j];
            matrix[j] = matrix[i];
            matrix[i] = temp;
            j++;
        } else {
            const temp = matrix[i];
            matrix[i] = matrix[down];
            matrix[down] = temp;
            down--;
            i--;
        }
    }
    return [j, down];
}


function compareLines(l1, l2) {

    for (let i = 0; i < l1.length; i++) {
        if (l1[i] === l2[i])
            continue;
        if (l1[i] === null)
            return true;
        if (l2[i] === null)
            return false;
        return l1[i] < l2[i];
    }
    return null;
}


function main() {

    const myArgs = process.argv.slice(2);
    let filename;
    if (myArgs.length === 0)
        filename = prompt("Read the file name: ");
    else
        filename = myArgs[0];
    readFromFile("files\\" + filename);
    console.log('Initial matrix');
    printMatrix();
    randomizedQuickSort3(0, matrix.length - 1);
    console.log('Sorted matrix');
    printMatrix();
}

main()
