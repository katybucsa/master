package service;

import model.Book;
import model.Loan;
import model.Review;
import model.User;

import java.util.List;

public interface Service {

    boolean validateLogin(String username, String password);

    User findUserByUsername(String username);

    List<Book> findAllBooks();

    List<Loan> findAllLoansForReader(String username);

    Book findBookById(int bookId);

    List<Review> findAllReviewsByBookId(int bookId);

    void addReview(String username, int bookId, String content, int rating);

    Book findBookByIsbn(String isbn);

    void addLoan(int bookId, String badgeId);

    Loan findCurrentLoanForBook(int bookId);

    void updateLoan(int bookId);

    void addBook(String title, String author, String isbn, int publishedYear);

    void deleteBook(int bookId);
}
