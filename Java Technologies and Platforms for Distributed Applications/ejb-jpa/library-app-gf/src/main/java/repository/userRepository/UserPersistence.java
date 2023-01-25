package repository.userRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity(name = "USER")
public class UserPersistence implements Serializable {

    @Id
    @Column(name = "username", length = 32)
    private String username = "";

    @Column(name = "password")
    private String password = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.READER;
}
