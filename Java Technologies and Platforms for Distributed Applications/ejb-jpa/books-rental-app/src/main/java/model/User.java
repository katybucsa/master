package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import repository.userRepository.Role;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    private String username;

    private String password;

    private Role role;
}
