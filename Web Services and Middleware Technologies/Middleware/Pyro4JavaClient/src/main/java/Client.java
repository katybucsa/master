import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroProxy;

import java.io.IOException;

public class Client {

    public static void main(String[] args) throws IOException {
        NameServerProxy ns= NameServerProxy.locateNS(null);
        PyroProxy proxy = new PyroProxy(ns.lookup("service"));
        Console console = new Console(proxy);
        console.run();
    }
}
