package service.impl;

import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.bookRepository.BookRepository;
import repository.loanRepository.LoanRepository;
import repository.personRepository.PersonRepository;
import repository.reviewRepository.ReviewRepository;
import repository.userRepository.UserRepository;
import service.Service;
import service.ServiceR;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Objects;

@Stateless
@Local(Service.class)
@Remote(ServiceR.class)
public class ServiceImpl implements Service, ServiceR {

    @EJB
    private BookRepository bookRepository;

    @EJB
    private LoanRepository loanRepository;

    @EJB
    private PersonRepository personRepository;

    @EJB
    private ReviewRepository reviewRepository;

    @EJB
    private UserRepository userRepository;

    private final Logger logger = LogManager.getLogger(ServiceImpl.class);

    @Override
    public boolean validateLogin(String username, String password) {

        User user = userRepository.findUserByUsernameAndPassword(username, password);
        return !Objects.isNull(user);
    }

    @Override
    public User findUserByUsername(String username) {

        return userRepository.findUserByUsername(username);
    }

    @Override
    public List<Book> findAllBooks() {

        return bookRepository.findAllBooks();

    }

    @Override
    public List<Loan> findAllLoansForReader(String username) {

        Person person = personRepository.findPersonByUsername(username);
        return loanRepository.findAllLoansByPersonId(person.getBadgeId());
    }

    @Override
    public Book findBookById(int bookId) {

        return bookRepository.findBookById(bookId);
    }

    @Override
    public List<Review> findAllReviewsByBookId(int bookId) {

        return reviewRepository.findAllReviewsByBookId(bookId);
    }

    @Override
    public void addReview(String username, int bookId, String content, int rating) {

        Person person = personRepository.findPersonByUsername(username);
        Book book = bookRepository.findBookById(bookId);
        Review review = Review.builder()
                .person(person)
                .book(book)
                .content(content)
                .rating(rating)
                .build();
        reviewRepository.addReview(review);
    }

    @Override
    public Book findBookByIsbn(String isbn) {

        return bookRepository.findBookByIsbn(isbn);
    }

    @Override
    public void addLoan(int bookId, String badgeId) {

        Person person = personRepository.findPersonById(badgeId);
        Book book = bookRepository.findBookById(bookId);
        Loan loan = Loan.builder()
                .person(person)
                .book(book)
                .build();
        loanRepository.addLoan(loan);
    }

    @Override
    public Loan findCurrentLoanForBook(int bookId) {

        return loanRepository.findCurrentLoanForBook(bookId);
    }

    @Override
    public void updateLoan(int bookId) {

        loanRepository.updateLoan(bookId);
    }

    @Override
    public void addBook(String title, String author, String isbn, int publishedYear) {

        Book book=Book.builder()
                .title(title)
                .author(author)
                .publishedYear(publishedYear)
                .isbn(isbn)
                .build();
        bookRepository.addBook(book);
    }

    @Override
    public void deleteBook(int bookId) {

        bookRepository.deleteBook(bookId);
    }
}
