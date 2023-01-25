package ro.mfpc.twopl.model.book;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    @Column(name = "name")
    private String name;

    @Column(name = "page_count")
    private int pageCount;

    @Column(name = "author_id")
    private int authorId;

    @Column(name = "borrowed")
    private boolean borrowed;
}
