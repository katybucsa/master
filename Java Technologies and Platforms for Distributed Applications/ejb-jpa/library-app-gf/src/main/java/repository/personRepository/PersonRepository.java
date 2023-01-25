package repository.personRepository;

import model.Person;
import util.Converters;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class PersonRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    public Person findPersonByUsername(String username) {

        TypedQuery<PersonPersistence> query = manager.createQuery("select p from PERSON p where p.user.username=:username", PersonPersistence.class);
        query.setParameter("username", username);
        return Converters.personPersistenceToPerson(query.getSingleResult());
    }


    public Person findPersonById(String id) {

        TypedQuery<PersonPersistence> query = manager.createQuery("select p from PERSON p where p.badgeId=:badgeId", PersonPersistence.class);
        query.setParameter("badgeId", id);
        return Converters.personPersistenceToPerson(query.getSingleResult());
    }
}
