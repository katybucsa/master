package ro.mfpc.twopl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mfpc.twopl.dto.*;
import ro.mfpc.twopl.model.author.Author;
import ro.mfpc.twopl.model.book.Book;
import ro.mfpc.twopl.model.borrow.Borrow;
import ro.mfpc.twopl.model.student.Student;
import ro.mfpc.twopl.service.Service;

@RestController
public class Controller {

    @Autowired
    private Service service;

    @RequestMapping(value = "/authors", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Author> addAuthor(@RequestBody AuthorDto authorDto) {

        Author added = service.addAuthor(authorDto.getName());
        return new ResponseEntity<>(added, HttpStatus.OK);
    }

    @RequestMapping(value = "/authors", method = RequestMethod.GET)
    public ResponseEntity<AuthorsToSendDto> getAuthors() {

        return new ResponseEntity<>(service.getAllAuthors(), HttpStatus.OK);
    }

    @RequestMapping(value = "/students", method = RequestMethod.GET)
    public ResponseEntity<StudentsToSendDto> getStudents() {

        return new ResponseEntity<>(service.getAllStudents(), HttpStatus.OK);
    }

    @RequestMapping(value = "/students", method = RequestMethod.POST)
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {

        return new ResponseEntity<>(service.addStudent(student), HttpStatus.OK);
    }

    @RequestMapping(value = "/students/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Student> deleteStudent(@PathVariable int id) {

        return new ResponseEntity<>(service.deleteStudent(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/borrows", method = RequestMethod.POST)
    public ResponseEntity<Borrow> addBorrow(@RequestBody BorrowDto borrowDto) {

        return new ResponseEntity<>(service.addBorrow(borrowDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public ResponseEntity<BooksToSendDto> getBooks(@RequestParam String filter) {

        return new ResponseEntity<>(service.getAllBooks(filter), HttpStatus.OK);
    }

    @RequestMapping(value = "/books/returns/{bookId}", method = RequestMethod.PUT)
    public ResponseEntity<Book> returnBook(@PathVariable int bookId) {

        return new ResponseEntity<>(service.returnBook(bookId), HttpStatus.OK);
    }

    @RequestMapping(value = "/books", method = RequestMethod.POST)
    public ResponseEntity<Book> addBook(@RequestBody BookDto bookDto) {

        return new ResponseEntity<>(service.addBook(bookDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Book> deleteBook(@PathVariable int id) {

        return new ResponseEntity<>(service.deleteBook(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/books/student/{id}", method = RequestMethod.GET)
    public ResponseEntity<StudentBorrowsDto> getStudentBorrows(@PathVariable int id) {

        return new ResponseEntity<>(service.getStudentBorrows(id), HttpStatus.OK);
    }
}
