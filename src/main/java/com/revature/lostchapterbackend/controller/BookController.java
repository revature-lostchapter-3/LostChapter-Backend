package com.revature.lostchapterbackend.controller;

import java.security.InvalidParameterException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.lostchapterbackend.annotation.Admin;
import com.revature.lostchapterbackend.dto.AddOrUpdateBookDTO;
import com.revature.lostchapterbackend.exceptions.BookNotFoundException;
import com.revature.lostchapterbackend.exceptions.GenreNotFoundException;
import com.revature.lostchapterbackend.exceptions.ISBNAlreadyExists;
import com.revature.lostchapterbackend.exceptions.SaleDiscountRateException;
import com.revature.lostchapterbackend.exceptions.SynopsisInputException;
import com.revature.lostchapterbackend.model.Book;
import com.revature.lostchapterbackend.service.BookService;
import com.revature.lostchapterbackend.service.UserService;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BookController {

	private Logger logger = LoggerFactory.getLogger(BookController.class);

	@Autowired
	private BookService bs;

	@GetMapping(path = "/books")
	public List<Book> getAllBooks() {
		logger.info("BookController.getAllBooks() invoked.");

		List<Book> bookList = bs.getAllBooks();

		return bookList;

	}

	@GetMapping(path = "/books/featured")
	public List<Book> getFeaturedBooks() {

		logger.info("BookController.getFeaturedBooks() invoked.");

		List<Book> featuredBooks = bs.getFeaturedBooks();

		return featuredBooks;
	}

	@GetMapping(path = "/books/{id}")
	public ResponseEntity<Object> getBookById(@PathVariable(value = "id") String id) {
		logger.info("BookController.getBookById() invoked.");

		try {
			Book book = bs.getBookById(id);
			return ResponseEntity.status(200).body(book);
		} catch (BookNotFoundException | InvalidParameterException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}

	}

	@GetMapping(path = "/books/genre/{genreId}")
	public ResponseEntity<Object> getBookByGenreId(@PathVariable(value = "genreId") String genreId) {
		logger.info("BookController.getBookByGenreId() invoked.");

		try {
			List<Book> bookList = bs.getBooksByGenreId(genreId);
			return ResponseEntity.status(200).body(bookList);
		} catch (InvalidParameterException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}

	}

	@GetMapping(path = "/books/search/{keyword}")
	public List<Book> getBookByKeyword(@PathVariable(value = "keyword") String keyword) {
		logger.info("BookController.getBookByKeyword() invoked.");

		return bs.getBooksByKeyword(keyword);

	}

	@GetMapping(path = "/books/sales")
	public List<Book> getBookBySale() {
		logger.info("BookController.getBookBySale() invoked.");

		return bs.getBooksBySale();

	}

	//@Admin
	@PostMapping(path = "/books")
	public ResponseEntity<Object> addNewBook(@RequestBody AddOrUpdateBookDTO dto, @RequestHeader(name = "Authorization") String token) throws SynopsisInputException {

		logger.info("BookController.addNewBook() invoked.");

		try {
			System.out.println("controller syso:" + dto.getSaleDiscountRate());
			Book addedBook = bs.addBook(dto);
			return ResponseEntity.status(201).body(addedBook);
		} catch (InvalidParameterException e) {
			return ResponseEntity.status(400).body(e.getMessage());

		} catch (GenreNotFoundException | ISBNAlreadyExists | SaleDiscountRateException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}
	}

	//@Admin
	@PutMapping(path = "/books/{id}")
	public ResponseEntity<Object> updateBookById(@PathVariable(value = "id") String id,
			@RequestBody AddOrUpdateBookDTO dto) throws InvalidParameterException, GenreNotFoundException,
			ISBNAlreadyExists, SynopsisInputException, SaleDiscountRateException {

		logger.info("BookController.updateBookById() invoked.");

		try {

			Book updatedBook = bs.updateBook(dto, id);

			logger.info("updatedBook {}", updatedBook);

			return ResponseEntity.status(200).body(updatedBook);

		} catch (BookNotFoundException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (InvalidParameterException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (GenreNotFoundException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (SaleDiscountRateException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (ISBNAlreadyExists e) {
			return ResponseEntity.status(400).body(e.getMessage());

		}

	}
}
