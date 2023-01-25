package ro.mfpc.twopl.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BorrowDto {

    int studentId;
    int bookId;
}
