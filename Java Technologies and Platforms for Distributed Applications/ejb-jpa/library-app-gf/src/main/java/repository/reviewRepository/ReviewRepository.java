package repository.reviewRepository;

import model.Review;
import util.Converters;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ReviewRepository {

    @PersistenceContext(unitName = "library")
    private EntityManager manager;

    public void addReview(Review review) {

        ReviewPersistence reviewPersistence = Converters.reviewToReviewPersistence(review);
        manager.persist(reviewPersistence);
    }

    public List<Review> findAllReviewsByBookId(int bookId) {

        TypedQuery<ReviewPersistence> query = manager.createQuery("select r from REVIEW r where r.book.id=:bookId", ReviewPersistence.class);
        query.setParameter("bookId", bookId);
        return query.getResultList()
                .stream()
                .map(Converters::reviewPersistenceToReviews)
                .collect(Collectors.toList());
    }
}
