package ro.mfpc.twopl.dto;

import lombok.*;
import ro.mfpc.twopl.model.author.Author;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthorsToSendDto {

    List<Author> data;
}
