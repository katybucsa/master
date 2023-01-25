import process.Process;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        String owner = "katy";
        int port1 = 5004, port2 = 5005, port3 = 5006;
        int index1 = 1, index2 = 2, index3 = 3;
        Process process1 = new Process(port1, owner, index1);
        Process process2 = new Process(port2, owner, index2);
        Process process3 = new Process(port3, owner, index3);
        new Thread(process1).start();
        new Thread(process2).start();
        new Thread(process3).start();
    }
}
