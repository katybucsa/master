package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import repository.bookRepository.BookPersistence;
import repository.personRepository.PersonPersistence;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Review implements Serializable {

    private int id;

    private Person person;

    private Book book;

    private String reviewerName;

    private String content;

    private int rating;

    private LocalDate publishedDate;
}
