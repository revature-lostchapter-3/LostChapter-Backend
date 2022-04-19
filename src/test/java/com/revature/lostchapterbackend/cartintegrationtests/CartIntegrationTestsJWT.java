package com.revature.lostchapterbackend.cartintegrationtests;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import com.revature.lostchapterbackend.dao.BookDAO;
import com.revature.lostchapterbackend.dao.GenreDAO;
import com.revature.lostchapterbackend.dto.SignUpDto;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.lostchapterbackend.dto.LoginDto;
import com.revature.lostchapterbackend.model.Book;
import com.revature.lostchapterbackend.model.BookToBuy;
import com.revature.lostchapterbackend.model.Carts;
import com.revature.lostchapterbackend.model.Genre;
import com.revature.lostchapterbackend.model.Users;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class CartIntegrationTestsJWT {
	@Autowired
	private WebApplicationContext webAppContext;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private GenreDAO genreDao;

	@Autowired
	private BookDAO bookDao;

	private MockMvc mvc;
	private static String testToken;

	private Genre fiction, nonfiction;

	private Users expectedUser;

	private SignUpDto testDto;

	private BookToBuy positiveBookToBuy;
	private BookToBuy negativeBookToBuy;

	public ArrayList<BookToBuy> b2b;

	@BeforeEach
	public void setup() {
		this.b2b = new ArrayList<>();

		Book positiveBook = new Book();    //Id should be 1
		Book noStockBook = new Book();    //Id should be 2

		mvc = MockMvcBuilders
				.webAppContextSetup(webAppContext)
				.addFilters(springSecurityFilterChain)
				.build();

		fiction = new Genre();
		fiction.setGenre("fiction");

		nonfiction = new Genre();
		nonfiction.setGenre("nonfiction");

		genreDao.save(fiction);
		genreDao.save(nonfiction);

		positiveBook = new Book("1234567879", "bookName", "synopsis",
				"author", fiction, 1, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");

		noStockBook = new Book("2122232425", "bookName2", "synopsis",
				"author", fiction, 0, 1996, "edition",
				"publisher", true,
				0.99, 10.99, "");

		bookDao.save(positiveBook);
		bookDao.save(noStockBook);



		testDto = new SignUpDto("test123", "password",
				"testfn", "testln", 21, "test123@gmail.com",
				"09/12/1990", "address123", "Customer");

		expectedUser = new Users(
				"test",
				"password",
				"testFirstName",
				"testLastName",
				21,
				"test@aol.com",
				"09/08/1990",
				"addresswest",
				"Customer");

		String signUpJson = "";
		try {
			signUpJson = mapper.writeValueAsString(testDto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String response = null;
		try {
			response = mvc.perform(post("/signup")
							.contentType(MediaType.APPLICATION_JSON)
							.content(signUpJson))
					.andReturn().getResponse().getContentAsString();
			System.out.println("mvc: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!response.equals("Email already exist.")) {
			testToken = response.substring(14, response.length() - 2);
		}

		this.positiveBookToBuy = new BookToBuy();
		this.negativeBookToBuy = new BookToBuy();

		this.positiveBookToBuy.setBooks(positiveBook);
		this.positiveBookToBuy.setId(1);
		this.positiveBookToBuy.setQuantityToBuy(1);

		this.negativeBookToBuy.setBooks(noStockBook);
		this.negativeBookToBuy.setId(2);
		this.negativeBookToBuy.setQuantityToBuy(1);

		this.b2b.add(positiveBookToBuy);
		//this.b2b.add(negativeBookToBuy);

	}

	@Test
	public void cart_test_adding_item_to_cart_positive() throws Exception {
		positiveBookToBuy.setId(1);
		String expectedJson = mapper.writeValueAsString(positiveBookToBuy);
		System.out.println("Expected Json: " + expectedJson);
		System.out.println("testToken: " + testToken);

		Carts expectedCart = new Carts();
		expectedCart.setUser(this.expectedUser);
		expectedCart.setCartId(1);
		expectedCart.setBooksToBuy(b2b);

		System.out.println(expectedCart.toString());

		mvc.perform(post("/users/1/cart")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(jsonToSend)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + testToken)
						.param("userId", "1")
						.param("bookId", "1")
						.param("quantityToBuy", "1"))
				.andExpect(content().json(expectedJson)).andExpect(status().is(200));

	}
//
//	@Test
//	public void cart_test_removing_item_from_cart_positive() throws Exception {
//
//
//
//	}
//
//
//	@Test
//	public void cart_test_attempting_to_add_to_cart_item_out_of_stock_negative() throws Exception {
//
//	}
//
//	@Test
//	public void cart_test_adding_item_to_cart_when_item_already_in_cart_positive() throws Exception {
//
//	}
//
//	@Test
//	public void cart_test_adding_item_of_multiple_quantity_positive() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_adding_item_of_negative_quantity_negative() throws Exception {
//
//	}
//
//	@Test
//	public void cart_test_get_cart_by_id_positive() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_get_cart_no_matching_id_negative() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_delete_book_in_cart_positive() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_delete_book_not_in_cart_negative() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_delete_all_items_in_cart_positive() throws Exception {
//
//	}
//
//	@Test
//	public void cart_test_add_book_to_cart_user_id_doesnt_match_pattern_negative() throws Exception {
//
//	}
//
//	@Test
//	public void cart_test_add_book_to_cart_bookId_doesnt_match_pattern_negative() throws Exception {
//}
//
//	@Test
//	public void cart_test_add_book_to_cart_quantityToBuy_doesnt_match_pattern_negative() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_trying_to_add_book_that_doesnt_exist_negative() throws Exception {
//
//	}
//
//
//	@Test
//	public void cart_test_trying_to_access_cart_that_doesnt_exist_negative() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_trying_to_get_cart_cart_id_not_int_negative() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_delete_item_in_cart_cartId_doesnt_match_pattern_negative() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_delete_item_in_cart_bookId_doesnt_match_pattern_negative() throws Exception {
//
//
//	}
//
//	@Test
//	public void cart_test_delete_item_in_cart_bookId_doesnt_exist_negative() throws Exception {
//
//	}
//
//	@Test
//	public void cart_test_delete_item_in_cart_cartId_doesnt_exist_negative() throws Exception {
//
//
//	}

}
