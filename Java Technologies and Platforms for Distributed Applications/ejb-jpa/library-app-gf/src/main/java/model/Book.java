package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Book implements Serializable {

    private int id;

    private String title;

    private String author;

    private int publishedYear;

    private String isbn;
}
