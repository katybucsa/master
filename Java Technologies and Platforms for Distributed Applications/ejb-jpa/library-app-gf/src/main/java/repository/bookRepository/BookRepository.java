package repository.bookRepository;

import model.Book;
import util.Converters;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class BookRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    public List<Book> findAllBooks() {

        TypedQuery<BookPersistence> query = manager.createQuery("select b from BOOK b", BookPersistence.class);
        return query.getResultList()
                .stream()
                .map(Converters::bookPersistenceToBook)
                .collect(Collectors.toList());
    }

    public Book findBookById(int bookId) {

        TypedQuery<BookPersistence> query = manager.createQuery("select b from BOOK b where b.id=:id", BookPersistence.class);
        query.setParameter("id", bookId);
        return query.getResultList().isEmpty() ? null : Converters.bookPersistenceToBook(query.getSingleResult());
    }

    public Book findBookByIsbn(String isbn) {

        TypedQuery<BookPersistence> query = manager.createQuery("select b from BOOK b where b.isbn=:isbn", BookPersistence.class);
        query.setParameter("isbn", isbn);
        return query.getResultList().isEmpty() ? null : Converters.bookPersistenceToBook(query.getSingleResult());
    }

    public void addBook(Book book) {

        BookPersistence bookPersistence = Converters.bookToBookPersistence(book);
        manager.persist(bookPersistence);
    }

    public void deleteBook(int bookId) {

        Query query = manager.createQuery("delete from BOOK b where b.id=:bookId");
        query.setParameter("bookId", bookId);
        query.executeUpdate();
    }
}
