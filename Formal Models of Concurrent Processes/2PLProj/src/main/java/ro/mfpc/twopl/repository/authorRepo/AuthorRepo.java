package ro.mfpc.twopl.repository.authorRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mfpc.twopl.model.author.Author;

public interface AuthorRepo extends JpaRepository<Author, Integer> {
}
