package ro.mfpc.twopl.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookToSendDto {

    private int bookId;

    private String name;

    private int pageCount;

    private String authorName;

    private boolean borrowed;
}
