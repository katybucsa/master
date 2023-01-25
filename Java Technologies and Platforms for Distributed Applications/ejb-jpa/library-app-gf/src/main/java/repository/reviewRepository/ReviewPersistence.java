package repository.reviewRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import repository.bookRepository.BookPersistence;
import repository.personRepository.PersonPersistence;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "REVIEW")
public class ReviewPersistence implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id = 0;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonPersistence person = null;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookPersistence book = null;

    @Column(name = "content")
    private String content = "";

    @Column(name = "rating")
    private int rating = 1;

    @Column(name = "published_date")
    private LocalDate publishedDate = LocalDate.now();

    @PrePersist
    private void setPd() {
        this.publishedDate = LocalDate.now();
    }
}
