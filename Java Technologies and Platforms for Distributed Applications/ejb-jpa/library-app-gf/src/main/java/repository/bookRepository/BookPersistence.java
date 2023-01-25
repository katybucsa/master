package repository.bookRepository;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "BOOK")
public class BookPersistence implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id = 0;

    @Column(name = "title")
    private String title = "";

    @Column(name = "author")
    private String author = "";

    @Column(name = "published_year")
    private int publishedYear = 1000;

    @Column(name = "isbn")
    private String isbn = "";
}

