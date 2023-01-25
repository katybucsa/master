package ro.mfpc.twopl.repository.bookRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mfpc.twopl.model.book.Book;

import java.util.List;

public interface BookRepo extends JpaRepository<Book, Integer> {

    List<Book> findAllByBorrowed(boolean borrowed);
}
