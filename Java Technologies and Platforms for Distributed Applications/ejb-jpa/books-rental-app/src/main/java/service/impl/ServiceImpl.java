package service.impl;

import model.Book;
import model.Loan;
import model.Person;
import model.User;
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

        Person person=personRepository.findPersonByUsername(username);
        return null;
    }


//    public void addLoan(LoanPersistence loan) {
//
//        logger.info("========== LOGGING addLoan ==========");
//
////        LoanPersistence savedLoan = loanRepository.save(loan);
//
//        logger.info("==========SUCCESSFULLY LOGGING addLoan ==========");
//        return null;
//    }

}
