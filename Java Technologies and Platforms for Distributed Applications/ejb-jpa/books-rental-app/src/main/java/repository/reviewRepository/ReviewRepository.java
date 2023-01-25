package repository.reviewRepository;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ReviewRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    public void addReview(ReviewPersistence review) {

        manager.persist(review);
    }

    public List<ReviewPersistence> getAllReviewsByBookId(Integer bookId) {

        TypedQuery<ReviewPersistence> query = manager.createQuery("select r from REVIEW r where r.book.id=:bookId", ReviewPersistence.class);
//        List<ReviewPersistence> reviewPersistences =
        return null;
    }
}
