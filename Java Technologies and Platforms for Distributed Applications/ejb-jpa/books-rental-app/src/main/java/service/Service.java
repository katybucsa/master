package service;

import model.Book;
import model.Loan;
import model.User;

import java.util.List;

public interface Service {

   boolean validateLogin(String username, String password);

    User findUserByUsername(String username);

    List<Book> findAllBooks();

    List<Loan> findAllLoansForReader(String username);
}
