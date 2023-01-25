package jndi;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class Book implements Serializable {

    private int id;

    private String title;

    private String author;

    private int publishedYear;

    private String isbn;
}
