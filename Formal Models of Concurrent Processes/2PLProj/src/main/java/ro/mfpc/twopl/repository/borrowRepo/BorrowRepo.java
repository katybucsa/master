package ro.mfpc.twopl.repository.borrowRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.mfpc.twopl.model.borrow.Borrow;

import java.math.BigInteger;
import java.util.List;

public interface BorrowRepo extends JpaRepository<Borrow, Integer> {

//    @Query("SELECT b FROM Borrow b WHERE b.bookId in :booksIds group by b.bookId order by b.broughtDate DESC )")
//    List<Borrow> selectLastBorrowForEachBook( @Param("booksIds") List<Integer> booksIds);

    List<Borrow> findBorrowByBookIdAndBroughtDate(int bookId, BigInteger broughtDate);

    List<Borrow> findBorrowsByStudentId(int studentId);
}
