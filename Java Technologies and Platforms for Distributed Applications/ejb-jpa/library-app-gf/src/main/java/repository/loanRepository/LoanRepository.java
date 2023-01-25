package repository.loanRepository;

import model.Loan;
import repository.bookRepository.BookPersistence;
import util.Converters;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class LoanRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    public List<Loan> findAllLoansByPersonId(String badgeId) {

        TypedQuery<LoanPersistence> query = manager.createQuery("select l from LOAN l where l.person.badgeId=:personId", LoanPersistence.class);
        query.setParameter("personId", badgeId);
        return query.getResultList()
                .stream()
                .map(Converters::loanPersistenceToLoan)
                .collect(Collectors.toList());
    }

    public void addLoan(Loan loan) {

        LoanPersistence loanPersistence = Converters.loanToLoanPersistence(loan);
        manager.persist(loanPersistence);
    }

    public Loan findCurrentLoanForBook(int bookId) {

        TypedQuery<LoanPersistence> query = manager.createQuery("select l from LOAN l where l.book.id=:bookId and l.returnDate is null", LoanPersistence.class);
        query.setParameter("bookId", bookId);
        return query.getResultList().isEmpty() ? null : Converters.loanPersistenceToLoan(query.getSingleResult());
    }

    public void updateLoan(int bookId) {

        Query query = manager.createQuery("update LOAN l set l.returnDate=:rd where l.book.id=:bookId and l.returnDate is null");
        query.setParameter("rd", LocalDate.now());
        query.setParameter("bookId", bookId);
        query.executeUpdate();
    }
}
