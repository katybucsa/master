package ro.mfpc.twopl.model.borrow;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "borrows")
@Data
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrow_id")
    private int borrowId;

    @Column(name = "student_id")
    private int studentId;

    @Column(name = "book_id")
    private int bookId;

    @Column(name = "taken_date")
    private BigInteger takenDate;

    @Column(name = "brought_date")
    private BigInteger broughtDate;
}
