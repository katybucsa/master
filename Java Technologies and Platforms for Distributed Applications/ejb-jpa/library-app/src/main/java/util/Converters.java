package util;

import model.*;
import repository.bookRepository.BookPersistence;
import repository.loanRepository.LoanPersistence;
import repository.personRepository.PersonPersistence;
import repository.reviewRepository.ReviewPersistence;
import repository.userRepository.UserPersistence;

import java.util.Objects;

public class Converters {

    public static Book bookPersistenceToBook(BookPersistence bookPersistence) {

        if (Objects.isNull(bookPersistence))
            return null;
        return Book.builder()
                .id(bookPersistence.getId())
                .author(bookPersistence.getAuthor())
                .isbn(bookPersistence.getIsbn())
                .publishedYear(bookPersistence.getPublishedYear())
                .title(bookPersistence.getTitle())
                .build();
    }

    public static User userPersistenceToUser(UserPersistence singleResult) {

        if (Objects.isNull(singleResult))
            return null;
        return User.builder()
                .username(singleResult.getUsername())
                .password(singleResult.getPassword())
                .role(singleResult.getRole())
                .build();
    }

    public static Person personPersistenceToPerson(PersonPersistence singleResult) {

        if (Objects.isNull(singleResult))
            return null;
        return Person.builder()
                .user(userPersistenceToUser(singleResult.getUser()))
                .address(singleResult.getAddress())
                .badgeId(singleResult.getBadgeId())
                .fullName(singleResult.getFullName())
                .build();
    }

    public static Loan loanPersistenceToLoan(LoanPersistence loanPersistence) {

        if (Objects.isNull(loanPersistence))
            return null;
        return Loan.builder()
                .id(loanPersistence.getId())
                .loanDate(loanPersistence.getLoanDate())
                .book(bookPersistenceToBook(loanPersistence.getBook()))
                .person(personPersistenceToPerson(loanPersistence.getPerson()))
                .returnDate(loanPersistence.getReturnDate())
                .build();
    }

    public static Review reviewPersistenceToReviews(ReviewPersistence reviewPersistence) {

        if (Objects.isNull(reviewPersistence))
            return null;
        return Review.builder()
                .id(reviewPersistence.getId())
                .person(personPersistenceToPerson(reviewPersistence.getPerson()))
                .book(bookPersistenceToBook(reviewPersistence.getBook()))
                .content(reviewPersistence.getContent())
                .rating(reviewPersistence.getRating())
                .publishedDate(reviewPersistence.getPublishedDate())
                .build();
    }

    public static PersonPersistence personToPersonPersistence(Person person) {

        if (Objects.isNull(person))
            return null;
        return PersonPersistence.builder()
                .badgeId(person.getBadgeId())
                .address(person.getAddress())
                .fullName(person.getFullName())
                .user(userToUserPersistence(person.getUser()))
                .build();
    }

    private static UserPersistence userToUserPersistence(User user) {

        if (Objects.isNull(user))
            return null;
        return UserPersistence.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    public static BookPersistence bookToBookPersistence(Book book) {

        if (Objects.isNull(book))
            return null;
        return BookPersistence
                .builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publishedYear(book.getPublishedYear())
                .isbn(book.getIsbn())
                .build();
    }

    public static ReviewPersistence reviewToReviewPersistence(Review review) {

        if (Objects.isNull(review))
            return null;
        return ReviewPersistence.builder()
                .person(personToPersonPersistence(review.getPerson()))
                .book(bookToBookPersistence(review.getBook()))
                .content(review.getContent())
                .rating(review.getRating())
                .publishedDate(review.getPublishedDate())
                .build();
    }

    public static LoanPersistence loanToLoanPersistence(Loan loan) {

        if (Objects.isNull(loan))
            return null;
        return LoanPersistence.builder()
                .book(bookToBookPersistence(loan.getBook()))
                .person(personToPersonPersistence(loan.getPerson()))
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .build();
    }
}
