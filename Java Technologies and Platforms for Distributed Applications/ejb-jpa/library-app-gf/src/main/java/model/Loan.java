package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Loan implements Serializable {

    private int id;

    private Person person;

    private Book book;

    private LocalDate loanDate;

    private LocalDate returnDate;
}
