package ro.mfpc.twopl.dto;

import lombok.*;

import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StudentBorrows {

    String bookName;
    BigInteger borrowDate;
    BigInteger returnDate;
}
