import node.Node;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        BasicConfigurator.configure();
        String owner = "katy";
        int port1 = 5004, port2 = 5005, port3 = 5006;
        int index1 = 1, index2 = 2, index3 = 3;
        Node node1 = new Node(port1, owner, index1);
        Node node2 = new Node(port2, owner, index2);
        Node node3 = new Node(port3, owner, index3);
        new Thread(node1).start();
        new Thread(node2).start();
        new Thread(node3).start();
    }
}
