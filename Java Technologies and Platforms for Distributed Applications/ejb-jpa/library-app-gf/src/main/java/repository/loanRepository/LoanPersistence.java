package repository.loanRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Book;
import model.Person;
import repository.bookRepository.BookPersistence;
import repository.personRepository.PersonPersistence;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "LOAN")
public class LoanPersistence implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id=0;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "person_id")
    private PersonPersistence person=null;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "book_id")
    private BookPersistence book=null;

    @Column(name = "loan_date")
    private LocalDate loanDate=LocalDate.now();

    @Column(name = "return_date")
    private LocalDate returnDate=null;

    @PrePersist
    private void setLDate() {

        this.loanDate = LocalDate.now();
    }
}
