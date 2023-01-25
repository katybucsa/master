package repository.userRepository;

import model.User;
import util.Converters;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Objects;

@Stateless
public class UserRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    public User findUserByUsernameAndPassword(String username, String password) {

        TypedQuery<UserPersistence> query = manager.createQuery("select u from USER u where u.username=:username and u.password=:password", UserPersistence.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.getResultList().isEmpty() ? null : Converters.userPersistenceToUser(query.getSingleResult());
    }

    public User findUserByUsername(String username) {

        TypedQuery<UserPersistence> query = manager.createQuery("select u from USER u where u.username=:username", UserPersistence.class);
        query.setParameter("username", username);
        return Converters.userPersistenceToUser(query.getSingleResult());
    }
}
