package ro.mfpc.twopl.service;

import ro.mfpc.twopl.dto.*;
import ro.mfpc.twopl.model.author.Author;
import ro.mfpc.twopl.model.book.Book;
import ro.mfpc.twopl.model.borrow.Borrow;
import ro.mfpc.twopl.model.student.Student;

import java.util.List;

public interface Service {

    Author addAuthor(String name);

    BooksToSendDto getAllBooks(String filter);

    StudentsToSendDto getAllStudents();

    AuthorsToSendDto getAllAuthors();

    Borrow addBorrow(BorrowDto borrowDto);

    Book returnBook(int bookId);

//    Borrow updateBorrow(int borrowId);

//    Book getOneBook(int bookId);

    Book addBook(BookDto bookDto);

    Book deleteBook(int id);

    Student addStudent(Student student);

    Student deleteStudent(int id);

    StudentBorrowsDto getStudentBorrows(int id);

//    List<Book> filterBooksByBorrowedOrAvailable(String )


}
