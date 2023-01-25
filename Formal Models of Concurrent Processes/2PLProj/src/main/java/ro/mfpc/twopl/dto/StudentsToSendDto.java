package ro.mfpc.twopl.dto;

import lombok.*;
import ro.mfpc.twopl.model.student.Student;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StudentsToSendDto {

    List<Student> data;
}
