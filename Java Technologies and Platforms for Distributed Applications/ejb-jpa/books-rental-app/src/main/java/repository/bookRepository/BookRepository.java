package repository.bookRepository;

import mappers.Mappers;
import model.Book;
import repository.userRepository.UserPersistence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
public class BookRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    private Mappers mappers = org.mapstruct.factory.Mappers.getMapper(Mappers.class);

    public List<Book> findAllBooks() {

        TypedQuery<BookPersistence> query = manager.createQuery("select b from BOOK b", BookPersistence.class);
        return query.getResultList()
                .stream()
                .map(this::bookPersistenceToBook)
                .collect(Collectors.toList());
//        return mappers.booksPersistenceToBooks(query.getResultList());
    }


    private Book bookPersistenceToBook(BookPersistence bookPersistence) {

        if (Objects.isNull(bookPersistence))
            return null;
        return Book.builder()
                .id(bookPersistence.getId())
                .author(bookPersistence.getAuthor())
                .isbn(bookPersistence.getIsbn())
                .publishedYear(bookPersistence.getPublishedYear())
                .title(bookPersistence.getTitle())
                .build();
    }
}
