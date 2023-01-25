package service;

import model.Book;
import model.User;

import java.util.List;

public interface ServiceR {

    boolean validateLogin(String username, String password);

    User findUserByUsername(String username);

    List<Book> findAllBooks();
}
