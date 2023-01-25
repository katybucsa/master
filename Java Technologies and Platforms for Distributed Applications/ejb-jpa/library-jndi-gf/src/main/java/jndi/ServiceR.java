package jndi;

import jndi.Book;

import java.util.List;

public interface ServiceR {

    List<Book> findAllBooks();

    void addBook(String title, String author, String isbn, int publishedYear);
}
