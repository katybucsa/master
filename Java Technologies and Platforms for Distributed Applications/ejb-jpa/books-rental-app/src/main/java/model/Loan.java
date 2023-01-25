package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Loan {

    private int id;

    private String personId;

    private int bookId;

    private LocalDate loanDate;

    private LocalDate returnDate;
}
