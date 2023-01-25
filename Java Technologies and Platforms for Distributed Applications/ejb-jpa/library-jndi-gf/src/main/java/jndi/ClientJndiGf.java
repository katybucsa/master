package jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class ClientJndiGf {
    static final Properties JNDI = new Properties();

    static {
        JNDI.put("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
        JNDI.put("org.omg.CORBA.ORBInitialHost", "localhost");
        JNDI.put("org.omg.CORBA.ORBInitialPort", "3700");
    }

    static final String JNDIName = "java:global/library-app-gf/ServiceImpl!service.ServiceR";

    public static void main(String[] args) throws Exception {

        Context context=new InitialContext(JNDI);
        ServiceR proxy = (ServiceR) context.lookup(JNDIName);
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
