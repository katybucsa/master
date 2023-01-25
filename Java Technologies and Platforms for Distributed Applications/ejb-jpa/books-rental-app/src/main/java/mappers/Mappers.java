package mappers;


import model.Book;
import org.mapstruct.Mapper;
import repository.bookRepository.BookPersistence;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Mappers {

    List<Book> booksPersistenceToBooks(List<BookPersistence> booksPersistence);
}
