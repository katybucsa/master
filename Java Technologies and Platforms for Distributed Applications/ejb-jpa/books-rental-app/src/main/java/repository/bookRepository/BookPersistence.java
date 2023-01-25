package repository.bookRepository;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "BOOK")
public class BookPersistence {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name="author")
    private String author;

    @Column(name = "published_year")
    private int publishedYear;

    @Column(name = "isbn")
    private String isbn;
}

