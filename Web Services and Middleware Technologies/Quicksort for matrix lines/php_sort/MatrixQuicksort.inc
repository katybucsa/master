<?php

class MatrixQuicksort
{
    public $matrix = [];

    function readFromFile($filename)
    {
        $rows = 0;
        $columns = 0;
        $strings = [];
        if ( !file_exists($filename) ) {
            echo("Read from file error!\n");
            return;
        }
        $file = fopen($filename, 'r');
        while (!feof($file)) {
            $line = trim(fgets($file), " \n\r\t\v\0");
            $s = explode(',', $line);
            if (count($s) != 3) {
                echo "\nIncorrect line\n";
                continue;
            }
            if ((int)$s[0] > $rows)
                $rows = (int)$s[0];
            if ((int)$s[1] > $columns)
                $columns = (int)$s[1];
            array_push($strings, $line);
        }
        fclose($file);
        $rows++;
        $columns++;
        for ($i = 0; $i < $rows; $i++) {
            $line = array_fill(0, $columns, null);
            array_push($this->matrix, $line);
        }

        foreach ($strings as $s) {
            $strs = explode(',', $s);
            $l = (int)$strs[0];
            $c = (int)$strs[1];
            $this->matrix[$l][$c] = $strs[2];
        }
    }

    function printMatrix()
    {
        foreach ($this->matrix as $strings) {
            echo implode(" ", $strings);
            echo "\n";
        }
    }

    function randomizedQuicksort($up, $down)
    {
        if ($up >= $down)
            return;

        $k = $up + rand() % ($down - $up + 1);
        $temp = $this->matrix[$k];
        $this->matrix[$k] = $this->matrix[$up];
        $this->matrix[$up] = $temp;
        $positions = $this->partition3($up, $down);

        $this->randomizedQuicksort($up, $positions[0] - 1);
        $this->randomizedQuicksort($positions[1] + 1, $down);
    }

    function partition3($up, $down)
    {
        $x = $this->matrix[$up];
        $j = $up;
        for ($i = $up + 1; $i <= $down; $i++) {

            $comp = $this->compareLines($this->matrix[$i], $x);
            if ($comp === null) continue;
            if ($comp === true) {
                $temp = $this->matrix[$j];
                $this->matrix[$j] = $this->matrix[$i];
                $this->matrix[$i] = $temp;
                $j++;
            } else {
                $temp = $this->matrix[$i];
                $this->matrix[$i] = $this->matrix[$down];
                $this->matrix[$down] = $temp;
                $down--;
                $i--;
            }
        }
        return [$j, $down];
    }

    function compareLines($l1, $l2)
    {
        for ($i = 0; $i < count($l1); $i++) {
            if ($l1[$i] === $l2[$i])
                continue;
            if ($l1[$i] === null)
                return true;
            if ($l2[$i] === null)
                return false;
            return strcmp($l1[$i], $l2[$i]) < 0;
        }
        return null;
    }

    function main()
    {
        global $argc, $argv;
        if ($argc === 1)
            $filename = "files\\" . readline('Read the file name:');
        else
            $filename = "files\\" . $argv[1];
        echo "\nInitial matrix:\n";
        $this->readFromFile($filename);
        $this->printMatrix();
        $this->randomizedQuicksort(0, count($this->matrix) - 1);
        echo "\nSorted matrix:\n";
        $this->printMatrix();
    }
}

$mq = new MatrixQuicksort();
$mq->main();
