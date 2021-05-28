import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MatrixQuickSort {

    private static final List<List<String>> matrix = new ArrayList<>();

    private static void readFromFile(String filename) {

        int rows = 0;
        int columns = 0;
        List<String> strings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("[,]");
                if (s.length != 3) {
                    System.out.println("Incorrect line");
                    continue;
                }
                if (Integer.parseInt(s[0]) > rows)
                    rows = Integer.parseInt(s[0]);
                if (Integer.parseInt(s[1]) > columns)
                    columns = Integer.parseInt(s[1]);
                strings.add(line);
            }
        } catch (IOException e) {
            System.out.println("Read from file error!\n");
        }
        rows++;
        columns++;
        for (int i = 0; i < rows; i++) {
            List<String> line = new ArrayList<>(Collections.nCopies(columns, null));
            matrix.add(line);
        }
        for (String s : strings) {
            String[] strs = s.split("[,]");
            int l = Integer.parseInt(strs[0]);
            int c = Integer.parseInt(strs[1]);
            matrix.get(l).set(c, strs[2]);
        }
    }

    private static void printMatrix() {

        for (List<String> strings : matrix) {
            for (String str : strings) System.out.print(str + "  ");
            System.out.println();
        }
    }

    private static void randomizedQuickSort3(int up, int down) {

        if (up >= down)
            return;

        Random random = new Random();
        int k = up + Math.abs(random.nextInt()) % (down - up + 1);
        List<String> temp = matrix.get(k);
        matrix.set(k, matrix.get(up));
        matrix.set(up, temp);
        List<Integer> positions = partition3(up, down);

        randomizedQuickSort3(up, positions.get(0) - 1);
        randomizedQuickSort3(positions.get(1) + 1, down);
    }

    private static List<Integer> partition3(int up, int down) {

        List<String> x = matrix.get(up);
        int j = up;
        for (int i = up + 1; i <= down; i++) {
            Boolean comp = compareLines(matrix.get(i), x);
            if (comp == null) continue;
            if (comp) {
                List<String> temp = matrix.get(j);
                matrix.set(j, matrix.get(i));
                matrix.set(i, temp);
                j++;
            } else {
                List<String> temp = matrix.get(i);
                matrix.set(i, matrix.get(down));
                matrix.set(down, temp);
                down--;
                i--;
            }
        }
        return Arrays.asList(j, down);
    }

    private static Boolean compareLines(List<String> l1, List<String> l2) {

        for (int i = 0; i < l1.size(); i++) {
            if (l1.get(i).equals(l2.get(i)))
                continue;
            if (l1.get(i) == null)
                return true;
            if (l2.get(i) == null)
                return false;
            return l1.get(i).compareTo(l2.get(i)) < 0;
        }
        return null;
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String filename = "..\\files\\";
        if (args.length == 0) {
            System.out.println("Read the file name: ");
            filename += scanner.next();
        } else {
            filename += args[0];
        }
        System.out.println(filename);
        readFromFile(filename);
        System.out.println("\nInitial matrix");
        printMatrix();
        randomizedQuickSort3(0, matrix.size() - 1);
        System.out.println("\nSorted matrix");
        printMatrix();
    }
}
