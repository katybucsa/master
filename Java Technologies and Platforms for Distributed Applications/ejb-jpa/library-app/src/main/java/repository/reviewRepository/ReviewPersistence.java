package repository.reviewRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import repository.bookRepository.BookPersistence;
import repository.personRepository.PersonPersistence;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "REVIEW")
public class ReviewPersistence {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonPersistence person;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookPersistence book;

    @Column(name = "content")
    private String content;

    @Column(name = "rating")
    private int rating;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @PrePersist
    private void setPd() {
        this.publishedDate = LocalDate.now();
    }
}
