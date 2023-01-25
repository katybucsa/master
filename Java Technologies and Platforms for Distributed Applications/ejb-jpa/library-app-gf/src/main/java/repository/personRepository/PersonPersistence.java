package repository.personRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import repository.userRepository.UserPersistence;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "PERSON")
public class PersonPersistence implements Serializable {

    @Id
    @Column(name = "badge_id", length = 12)
    private String badgeId="";

    @Column(name = "address")
    private String address="";

    @Column(name = "full_name")
    private String fullName="";

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserPersistence user=null;
}
