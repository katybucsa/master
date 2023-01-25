package ro.mfpc.twopl.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookDto {

    String name;
    int pageCount;
    int authorId;
}
