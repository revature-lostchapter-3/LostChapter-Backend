package com.revature.lostchapterbackend.integrationtests;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.revature.lostchapterbackend.model.Users;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.revature.lostchapterbackend.dto.AddOrUpdateBookDTO;
import com.revature.lostchapterbackend.model.Book;
import com.revature.lostchapterbackend.model.Genre;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookIntegrationTest {

	@Autowired
	private MockMvc mvc; // send fake http requests to the server

	@Autowired
	private EntityManagerFactory emf;

	@Autowired
	private ObjectMapper mapper; // translate JSON objects

	private Genre g;
	private Genre g2;
	private Users admin;

	@BeforeEach
	public void setUp() {
		EntityManager em = emf.createEntityManager();
		Session session = em.unwrap(Session.class);
		Transaction tx = session.beginTransaction();

		g = new Genre();
		g.setGenre("Fiction");
		em.persist(g);
		g2 = new Genre();
		g2.setGenre("NonFiction");
		em.persist(g2);

		// Arrange
		Book actualBook = new Book("1234567879", "bookName", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");
		em.persist(actualBook);

		Book actualBook2 = new Book("2122232425", "bookName2", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99,10.99, "");
		em.persist(actualBook2);

		Book actualBook3 = new Book("91011121314", "bookName3", "synopsis",
				"author", g2, 1, 1996, "edition", "publisher",
				false, 0.99, 10.99, "");
		em.persist(actualBook3);

		admin = new Users("test123",
				"5E884898DA28047151D0E56F8DC6292773603D0D6AABBDD62A11EF721D1542D8",
				"testfn","testln",21,"test123@gmail.com","1990-12-09",
				"address123","Admin");
		em.persist(admin);

		tx.commit();
		session.close();

	}

	@Test
	public void testGetAllBook_positive() throws Exception {


		// Act and Assert
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books");

		//fix genre here also
		Book expectedBook = new Book("1234567879", "bookName", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");
		expectedBook.setBookId(1);
		Book expectedBook2 = new Book("2122232425", "bookName2", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99,10.99, ""); //book id 1
		expectedBook2.setBookId(2);
		Book expectedBook3 = new Book("91011121314", "bookName3", "synopsis",
				"author", g2, 1, 1996, "edition", "publisher",
				false, 0.99, 10.99, ""); //book id 1
		expectedBook3.setBookId(3);

		List<Book> expectedBooks = new ArrayList<>();
		expectedBooks.add(expectedBook);
		expectedBooks.add(expectedBook2);
		expectedBooks.add(expectedBook3);

		String expectedJson = mapper.writeValueAsString(expectedBooks);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(200))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));

	}
	
	@Test
	public void testGetBookById_positive() throws Exception {

		// Not sure how best to send id of 1
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books/1");

		//fix genre here also
		Book expectedBook = new Book("1234567879", "bookName", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");
		expectedBook.setBookId(1);

		String expectedJson = mapper.writeValueAsString(expectedBook);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(200))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));

	}

	@Test
	public void testGetAllBooksByGenre_positive() throws Exception {

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books/genre/1");

		Book expectedBook1 = new Book("1234567879", "bookName", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");
		expectedBook1.setBookId(1);
		Book expectedBook2 = new Book("2122232425", "bookName2", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99,10.99, "");
		expectedBook2.setBookId(2);

		List<Book> expectedBooks = new ArrayList<>();
		expectedBooks.add(expectedBook1);
		expectedBooks.add(expectedBook2);

		String expectedJson = mapper.writeValueAsString(expectedBooks);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(200))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	public void testGetBooksByKeyWord_positive() throws Exception {

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books/search/bookName");

		Book expectedBook1 = new Book("1234567879", "bookName", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");
		expectedBook1.setBookId(1);
		Book expectedBook2 = new Book("2122232425", "bookName2", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99,10.99, "");
		expectedBook2.setBookId(2);
		Book expectedBook3 = new Book("91011121314", "bookName3", "synopsis",
				"author", g2, 1, 1996, "edition", "publisher",
				false, 0.99, 10.99, "");
		expectedBook3.setBookId(3);

		List<Book> expectedBooks = new ArrayList<>();
		expectedBooks.add(expectedBook1);
		expectedBooks.add(expectedBook2);
		expectedBooks.add(expectedBook3);

		String expectedJson = mapper.writeValueAsString(expectedBooks);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(200))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));

	}

	@Test
	public void testGetBooksBySales_positive() throws Exception {

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books/sales");

		Book expectedBook1 = new Book("1234567879", "bookName", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");
		expectedBook1.setBookId(1);
		Book expectedBook2 = new Book("2122232425", "bookName2", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.99,10.99, "");
		expectedBook2.setBookId(2);

		List<Book> expectedBooks = new ArrayList<>();
		expectedBooks.add(expectedBook1);
		expectedBooks.add(expectedBook2);

		String expectedJson = mapper.writeValueAsString(expectedBooks);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(200))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));

	}

	@Test
	public void testAddBook_positive() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "synopsis",
				"author", 1, 1, 1996, "edition",
				"publisher", true,
				0.90, 10.99, "image");
		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		Book expectedBook1 = new Book("2425262728", "bookName4", "synopsis",
				"author", g, 1, 1996, "edition",
				"publisher", true,
				0.90, 10.99, "image");
		expectedBook1.setBookId(4);


		String expectedJson = mapper.writeValueAsString(expectedBook1);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(201))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));

	}

	@Test
	public void testAddBookISBNIsEmpty_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("", "bookName4", "synopsis",
				"author", 1, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "image");
		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("ISBN cannot be blank."));

	}

	@Test
	public void testAddBookButBookNameIsEmpty_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "", "synopsis",
				"author", 1, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "image");
		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Book name cannot be blank."));

	}

	@Test
	public void testAddBookSynopsisIsEmpty_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "",
				"author", 1, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "image");
		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Synopsis cannot be blank."));

	}

	@Test
	public void testAddBookAuthorIsEmpty_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "synopsis",
				"", 1, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "image");
		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Author cannot be blank."));

	}

	@Test
	public void testAddBookGenreIsZero_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "synopsis",
				"author", 0, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "image");
		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Genre cannot be blank."));

	}

//	@Test
//	public void testAddBookQuantityIsZero_negative() throws Exception {
//
//		AddOrUpdateBookDTO actualBook = new AddBookDTO("2425262728", "bookName4", "synopsis",
//				"author", 1, 0, 1996, "edition",
//				"publisher", true,
//				0.99, 10.99, "image");
//		String jsonToSend = mapper.writeValueAsString(actualBook);
//
//		MockHttpSession session1 = new MockHttpSession();
//
//		session1.setAttribute("currentUser", admin);
//
//		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
//				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
//
//		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
//				.andExpect(MockMvcResultMatchers.content().string("Quantity cannot be blank."));
//
//	}

	@Test
	public void testAddBookEditionIsEmpty_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "synopsis",
				"author", 1, 0, 1996, "",
				"publisher", true,
				0.99, 10.99, "image");

		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Edition cannot be blank."));
	}

	@Test
	public void testAddBookPublisherIsEmpty_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "synopsis",
				"author", 1, 0, 1996, "edition",
				"", true,
				0.99, 10.99, "image");

		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Publisher cannot be blank."));
	}

	@Test
	public void testAddBookImageIsEmpty_negative() throws Exception {

		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "synopsis",
				"author", 1, 0, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");

		String jsonToSend = mapper.writeValueAsString(actualBook);

		MockHttpSession session1 = new MockHttpSession();

		session1.setAttribute("currentUser", admin);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session1)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Image cannot be blank."));
	}
	
	@Test
	public void book_controller_test_get_book_by_id_bookNotFoundException_negative() throws Exception {
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books/-1");
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
				.andExpect(MockMvcResultMatchers.content().string("Book doesn't exist"));
		
	}
	
	@Test
	public void book_controller_test_add_book_genre_not_found_exception_negative() throws Exception {
		
		AddOrUpdateBookDTO bookToAdd = new AddOrUpdateBookDTO("12345", "bookname5", "synopsis", "author", 12345, 5, 1996, "edition", "publisher", true, 0.5, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(bookToAdd);
		
		MockHttpSession session = new MockHttpSession();
		
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
		.andExpect(MockMvcResultMatchers.content().string("Genre doesn't exist"));
		
	}
	
	@Test
	public void book_controller_test_add_book_ISNBAlreadyExistsException_negative() throws Exception {
		
		AddOrUpdateBookDTO bookToAdd = new AddOrUpdateBookDTO("1234567879", "bookname5", "synopsis", "author", 1, 5, 1996, "edition", "publisher", true, 0.5, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(bookToAdd);
		
		MockHttpSession session = new MockHttpSession(); 
		
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/books").session(session)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400))
		.andExpect(MockMvcResultMatchers.content().string("ISBN already used for another book"));
		
	}
	
	@Test
	public void book_controller_successfully_updating_book_positive() throws Exception {
		
		AddOrUpdateBookDTO bookToSend = new AddOrUpdateBookDTO("000001", "bookName", "synopsis",
				"author", 1, 5, 1996, "edition",
				"publisher", true,
				0.5, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(bookToSend);
		
		Book actualBook = new Book("000001", "bookName", "synopsis",
				"author", g, 5, 1996, "edition",
				"publisher", true,
				0.5, 10.99, "image");
		
		actualBook.setBookId(1);
		
		String expectedJson = mapper.writeValueAsString(actualBook);
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/books/1").session(session)
				.content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(200))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));
		
	}
	
	@Test
	public void book_controller_update_book_BookNotFoundException_negative() throws Exception {
		
		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("000001", "bookName", "synopsis",
				"author", 1, 5, 1996, "edition",
				"publisher", true,
				0.5, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(actualBook);
		
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/books/20").session(session).content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andExpect(MockMvcResultMatchers.content().string("Book doesn't exist"));
		
	}
	
	@Test
	public void book_controller_update_book_InvalidParamterException_negative() throws Exception {
		
		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("000001", "bookName", "synopsis",
				"author", 1, 5, 1996, "edition",
				"publisher", true,
				0.5, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(actualBook);
		
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/books/a").session(session).content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andExpect(MockMvcResultMatchers.content().string("Id must be in Int format"));
		
	}
	
	@Test
	public void book_controller_update_book_GenreNotFoundException_negative() throws Exception {
		
		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("000001", "bookName", "synopsis",
				"author", 20, 5, 1996, "edition",
				"publisher", true,
				0.5, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(actualBook);
		
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/books/1").session(session).content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andExpect(MockMvcResultMatchers.content().string("Genre doesn't exist"));
		
	}
	
	@Test
	public void book_controller_update_book_SaleDiscountRateException_negative() throws Exception {
		
		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("000001", "bookName", "synopsis",
				"author", 1, 5, 1996, "edition",
				"publisher", true,
				1, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(actualBook);
		
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/books/1").session(session).content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andExpect(MockMvcResultMatchers.content().string("Sale discount rate must be more than 0.0 and less than or equal to 0.90"));
		
	}
	
	@Test
	public void book_controller_update_book_ISNBAlreadyExistsException_negative() throws Exception {
		
		AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2122232425", "bookName", "synopsis",
				"author", 1, 5, 1996, "edition",
				"publisher", true,
				0.5, 10.99, "image");
		
		String jsonToSend = mapper.writeValueAsString(actualBook);
		
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("currentUser", admin);
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/books/1").session(session).content(jsonToSend).contentType(MediaType.APPLICATION_JSON);
		
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andExpect(MockMvcResultMatchers.content().string("ISBN already used for another book"));
		
	}
	
	@Test
	public void book_controller_get_book_by_id_InvalidParamterException_negative() throws Exception {	//Yall didnt catch this exception. Thrown in service layer, but not caught in controller layer.
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books/a"); 
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andExpect(MockMvcResultMatchers.content().string("The Id entered must be an int."));
		
	}
	
	@Test
	public void book_controller_get_books_by_genre_id_InvalidParamterException_negative() throws Exception { //Same thing here
		
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/books/genre/a");
		this.mvc.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andExpect(MockMvcResultMatchers.content().string("The genreId entered must be an int."));
		
	}
	
	
	


}
