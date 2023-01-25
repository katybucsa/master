package ro.mfpc.twopl.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import ro.mfpc.twopl.dto.*;
import ro.mfpc.twopl.model.author.Author;
import ro.mfpc.twopl.model.book.Book;
import ro.mfpc.twopl.model.borrow.Borrow;
import ro.mfpc.twopl.model.student.Student;
import ro.mfpc.twopl.repository.authorRepo.AuthorRepo;
import ro.mfpc.twopl.repository.bookRepo.BookRepo;
import ro.mfpc.twopl.repository.borrowRepo.BorrowRepo;
import ro.mfpc.twopl.repository.studentRepo.StudentRepo;
import ro.mfpc.twopl.service.Service;
import ro.mfpc.twopl.two_pl.LockType;
import ro.mfpc.twopl.two_pl.OperationType;
import ro.mfpc.twopl.two_pl.Transaction;
import ro.mfpc.twopl.two_pl.TwoPL;

import java.math.BigInteger;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private BorrowRepo borrowRepo;

    @Autowired
    private StudentRepo studentRepo;

    private final TwoPL twoPL = new TwoPL();

    @Override
    public Author addAuthor(String name) {

        Transaction transaction = twoPL.initTransaction();
        Author toSave = Author.builder().name(name).build();
        while (true) {
            if (!twoPL.lock(LockType.WRITE, -1, "authors", transaction, OperationType.INSERT, null, toSave, authorRepo))
                continue;
            authorRepo.save(toSave);
            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return toSave;
    }

    @Override
    public BooksToSendDto getAllBooks(String filter) {

        Transaction transaction = twoPL.initTransaction();
        List<Book> books;
        Map<Integer, Author> authors;

        while (true) {
            if (!twoPL.lock(LockType.READ, -1, "books", transaction, null, null, null, null))
                continue;
            if (filter.equals("all"))
                books = bookRepo.findAll();
            else if (filter.equals("borrowed"))
                books = bookRepo.findAllByBorrowed(true);
            else
                books = bookRepo.findAllByBorrowed(false);

            if (!twoPL.lock(LockType.READ, -1, "authors", transaction, null, null, null, null))
                continue;
            authors = authorRepo.findAllById(books.stream().map(Book::getAuthorId).collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.toMap(Author::getAuthorId, Function.identity()));

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }

        return BooksToSendDto.builder()
                .data(books.stream()
                        .map(b -> BookToSendDto.builder()
                                .bookId(b.getBookId())
                                .name(b.getName())
                                .pageCount(b.getPageCount())
                                .authorName(authors.get(b.getAuthorId()).getName())
                                .borrowed(b.isBorrowed())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public StudentsToSendDto getAllStudents() {

        Transaction transaction = twoPL.initTransaction();
        List<Student> students;

        while (true) {
            if (!twoPL.lock(LockType.READ, -1, "students", transaction, null, null, null, null))
                continue;
            students = studentRepo.findAll();

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return StudentsToSendDto.builder().data(students).build();
    }

    @Override
    public AuthorsToSendDto getAllAuthors() {

        Transaction transaction = twoPL.initTransaction();
        List<Author> authors;

        while (true) {
            if (!twoPL.lock(LockType.READ, -1, "authors", transaction, null, null, null, null))
                continue;
            authors = authorRepo.findAll();
            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return AuthorsToSendDto.builder().data(authors).build();
    }

    @Override
    public Borrow addBorrow(BorrowDto borrowDto) {

        Borrow toSave = Borrow.builder()
                .bookId(borrowDto.getBookId())
                .studentId(borrowDto.getStudentId())
                .takenDate(BigInteger.valueOf(Clock.systemDefaultZone().millis()))
                .build();

        Transaction transaction = twoPL.initTransaction();
        Borrow borrowSaved;
        while (true) {
            if (!twoPL.lock(LockType.READ, borrowDto.getBookId(), "borrows", transaction, null, null, null, null))
                continue;
            Book book = bookRepo.findById(borrowDto.getBookId()).get();

            if (!twoPL.lock(LockType.WRITE, book.getBookId(), "books", transaction, OperationType.UPDATE, book, null, bookRepo))
                continue;
            book.setBorrowed(true);
            bookRepo.save(book);

            if (!twoPL.lock(LockType.READ, borrowDto.getStudentId(), "students", transaction, null, null, null, null))
                continue;
            Student student = studentRepo.findById(borrowDto.getStudentId()).get();

            if (!twoPL.lock(LockType.WRITE, borrowDto.getStudentId(), "students", transaction, OperationType.UPDATE, student, null, studentRepo))
                continue;
            student.setNoBorrows(student.getNoBorrows() + 1);
            studentRepo.save(student);

            if (!twoPL.lock(LockType.WRITE, -1, "borrows", transaction, OperationType.INSERT, null, toSave, borrowRepo))
                continue;
            borrowSaved = borrowRepo.save(toSave);

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return borrowSaved;
    }

    @Override
    public Book returnBook(int bookId) {

        Transaction transaction = twoPL.initTransaction();
        Book book;

        while (true) {
            if (!twoPL.lock(LockType.READ, bookId, "books", transaction, null, null, null, null))
                continue;
            book = bookRepo.findById(bookId).get();

            if (!twoPL.lock(LockType.WRITE, book.getBookId(), "books", transaction, OperationType.UPDATE, book, null, bookRepo))
                continue;
            book.setBorrowed(false);
            bookRepo.save(book);

            if (!twoPL.lock(LockType.READ, -1, "borrows", transaction, null, null, null, null))
                continue;
            Borrow borrow = borrowRepo.findBorrowByBookIdAndBroughtDate(bookId, null).get(0);

            if (!twoPL.lock(LockType.WRITE, borrow.getBorrowId(), "borrows", transaction, OperationType.UPDATE, borrow, null, borrowRepo))
                continue;
            borrow.setBroughtDate(BigInteger.valueOf(Clock.systemDefaultZone().millis()));
            borrowRepo.save(borrow);

            if (!twoPL.lock(LockType.READ, borrow.getStudentId(), "students", transaction, null, null, null, null))
                continue;
            Student student = studentRepo.findById(borrow.getStudentId()).get();

            if (!twoPL.lock(LockType.WRITE, borrow.getStudentId(), "students", transaction, OperationType.UPDATE, student, null, studentRepo))
                continue;
            student.setNoBorrows(student.getNoBorrows() - 1);
            studentRepo.save(student);

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return book;
    }

    @Override
    public Book addBook(BookDto bookDto) {

        Book toSave = Book.builder()
                .name(bookDto.getName())
                .pageCount(bookDto.getPageCount())
                .authorId(bookDto.getAuthorId())
                .build();

        Transaction transaction = twoPL.initTransaction();
        Book book;

        while (true) {
            if (!twoPL.lock(LockType.WRITE, -1, "books", transaction, OperationType.INSERT, null, toSave, bookRepo))
                continue;
            book = bookRepo.save(toSave);

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return book;
    }

    @Override
    public Book deleteBook(int id) {

        Book toDelete = Book.builder().bookId(id).build();

        Transaction transaction = twoPL.initTransaction();

        while (true) {
            if (!twoPL.lock(LockType.WRITE, id, "books", transaction, OperationType.DELETE, toDelete, null, bookRepo))
                continue;
            bookRepo.delete(toDelete);

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }

        return toDelete;
    }

    @Override
    public Student addStudent(Student student) {

        Student toSave = Student.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .build();

        Transaction transaction = twoPL.initTransaction();
        while (true) {
            if (!twoPL.lock(LockType.WRITE, -1, "students", transaction, OperationType.INSERT, null, toSave, studentRepo))
                continue;

            studentRepo.save(toSave);
            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return toSave;
    }

    @Override
    public Student deleteStudent(int id) {

        Student toDelete = Student.builder().studentId(id).build();

        Transaction transaction = twoPL.initTransaction();

        while (true) {
            if (!twoPL.lock(LockType.WRITE, id, "students", transaction, OperationType.DELETE, toDelete, null, studentRepo))
                continue;
            studentRepo.delete(toDelete);

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }
        return toDelete;
    }

    @Override
    public StudentBorrowsDto getStudentBorrows(int id) {

        Transaction transaction = twoPL.initTransaction();
        List<Borrow> borrows;
        Map<Integer, Book> books;

        while (true) {
            if (!twoPL.lock(LockType.READ, -1, "borrows", transaction, null, null, null, null))
                continue;
            borrows = borrowRepo.findBorrowsByStudentId(id);

            if (!twoPL.lock(LockType.READ, -1, "books", transaction, null, null, null, null))
                continue;
            books = bookRepo.findAllById(borrows.stream().map(Borrow::getBookId).collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.toMap(Book::getBookId, Function.identity()));

            twoPL.releaseLocks(transaction);
            twoPL.commit(transaction);
            break;
        }

        return StudentBorrowsDto.builder()
                .data(borrows.stream()
                        .map(b -> StudentBorrows.builder()
                                .bookName(books.get(b.getBookId()).getName())
                                .borrowDate(b.getTakenDate())
                                .returnDate(b.getBroughtDate())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
