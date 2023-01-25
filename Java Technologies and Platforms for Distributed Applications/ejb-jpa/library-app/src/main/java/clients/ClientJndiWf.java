package clients;

import model.Book;
import service.ServiceR;

import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class ClientJndiWf {
    static final Properties JNDI = new Properties();

    static {
        JNDI.put("java.naming.factory.initial", "org.jboss.naming.remote.client.InitialContextFactory");
        JNDI.put("java.naming.provider.url", "http-remoting://localhost:8080");
    }

    static final String JNDIIdNameR = "library-app/ServiceImpl!service.ServiceR";

    public static void main(String[] args) throws Exception {
        ServiceR proxy = (ServiceR) (new InitialContext(JNDI)).lookup(JNDIIdNameR);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Titlu: ");
        String title = in.readLine();
        System.out.println("Autor: ");
        String author = in.readLine();
        System.out.println("Anul publicarii: ");
        int publishedYear = Integer.parseInt(in.readLine());
        System.out.print("ISBN:");
        String isbn = in.readLine();
        proxy.addBook(title, author, isbn, publishedYear);
        for (Book b : proxy.findAllBooks()) System.out.println(b);
    }
}
