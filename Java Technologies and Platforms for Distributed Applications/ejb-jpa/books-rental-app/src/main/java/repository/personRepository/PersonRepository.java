package repository.personRepository;

import model.Person;
import repository.userRepository.UserPersistence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Objects;

@Stateless
public class PersonRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    public Person findPersonByUsername(String username) {

        TypedQuery<PersonPersistence> query = manager.createQuery("select p from PERSON p where p.user.username=:username", PersonPersistence.class);
        query.setParameter("username", username);
        return personPersistenceToPerson(query.getSingleResult());

    }

    private Person personPersistenceToPerson(PersonPersistence singleResult) {

        if (Objects.isNull(singleResult))
            return null;
//        return Person.builder()
//                .user()
        return null;
    }
}
