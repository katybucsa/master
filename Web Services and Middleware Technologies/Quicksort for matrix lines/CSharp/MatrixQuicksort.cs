using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using static System.Int32;

namespace CSharp
{
    internal class MatrixQuickSort
    {
        private static List<List<string>> matrix = new List<List<string>>();

        private static void ReadFromFile(string filename)
        {
            var rows = 0;
            var columns = 0;
            var strings = new List<string>();
            using (TextReader tr = File.OpenText(filename))
            {
                string line;
                while ((line = tr.ReadLine()) != null)
                {
                    var s = line.Split(',');
                    if (s.Length != 3)
                    {
                        Console.WriteLine("Incorrect line");
                        continue;
                    }

                    if (Parse(s[0]) > rows)
                        rows = Parse(s[0]);
                    if (Parse(s[1]) > columns)
                        columns = Parse(s[1]);
                    strings.Add(line);
                }
            }

            rows++;
            columns++;
            for (var i = 0; i < rows; i++)
            {
                var line = new List<string>(columns);
                line.AddRange(Enumerable.Repeat<string>(null, columns));
                matrix.Add(line);
            }

            foreach (var s in strings)
            {
                var strs = s.Split(',');
                var l = Parse(strs[0]);
                var c = Parse(strs[1]);
                matrix[l][c] = strs[2];
            }
        }

        private static void PrintMatrix()
        {
            foreach (var strings in matrix)
            {
                foreach (var str in strings)
                    Console.Write(str + " ");
                Console.WriteLine();
            }
        }

        private static void RandomizedQuickSort3(int up, int down)
        {
            if (up >= down)
                return;

            var random = new Random();
            var k = up + random.Next() % (down - up + 1);
            var temp = matrix[k];
            matrix[k] = matrix[up];
            matrix[up] = temp;
            var positions = Partition3(up, down);

            RandomizedQuickSort3(up, positions[0] - 1);
            RandomizedQuickSort3(positions[1] + 1, down);
        }

        private static List<int> Partition3(int up, int down)
        {
            var x = matrix[up];
            var j = up;
            for (var i = up + 1; i <= down; i++)
            {
                var comp = CompareLines(matrix[i], x);
                if (comp == null) continue;
                if (comp.Equals(true))
                {
                    var temp = matrix[j];
                    matrix[j] = matrix[i];
                    matrix[i] = temp;
                    j++;
                }
                else
                {
                    var temp = matrix[i];
                    matrix[i] = matrix[down];
                    matrix[down] = temp;
                    down--;
                    i--;
                }
            }

            return new List<int>(new[] {j, down});
        }

        private static bool? CompareLines(List<string> l1, IReadOnlyList<string> l2)
        {
            for (var i = 0; i < l1.Capacity; i++)
            {
                if (l1[i].Equals(l2[i]))
                    continue;
                if (l1[i] == null)
                    return true;
                if (l2[i] == null)
                    return false;
                return string.Compare(l1[i], l2[i], StringComparison.Ordinal) < 0;
            }

            return null;
        }

        public static void Main(string[] args)
        {
            var filename = "files\\";
            if (args.Length == 0)
            {
                Console.WriteLine("Read the file name: ");
                filename += Console.ReadLine();
            }
            else
            {
                filename += args[0];
            }
            ReadFromFile(filename);
            Console.WriteLine("\nInitial matrix");
            PrintMatrix();
            RandomizedQuickSort3(0, matrix.Capacity - 1);
            Console.WriteLine("\nSorted matrix");
            PrintMatrix();
        }
    }
}