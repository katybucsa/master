package ro.mfpc.twopl.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StudentBorrowsDto {

    List<StudentBorrows> data;
}
