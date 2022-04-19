package com.revature.lostchapterbackend.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.lostchapterbackend.dao.BookDAO;
import com.revature.lostchapterbackend.dao.GenreDAO;
import com.revature.lostchapterbackend.dto.AddOrUpdateBookDTO;
import com.revature.lostchapterbackend.dto.SignUpDto;
import com.revature.lostchapterbackend.model.Book;
import com.revature.lostchapterbackend.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
public class BookIntegrationTestJwt {
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

    private Book testBook1, testBook2, testBook3;
    private Genre fiction, nonfiction;

    @BeforeEach
    public void setup() throws Exception {
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

        testBook1 = new Book("1234567879", "bookName", "synopsis",
                "author", fiction, 1, 1996, "edition",
                "publisher", true,
                0.99, 10.99, "");

        testBook2 = new Book("2122232425", "bookName2", "synopsis",
                "author", fiction, 1, 1996, "edition",
                "publisher", true,
                0.99, 10.99, "");

        testBook3 = new Book("91011121314", "bookName3", "synopsis",
                "author", nonfiction, 1, 1996, "edition", "publisher",
                false, 0.99, 10.99, "");

        bookDao.save(testBook1);
        bookDao.save(testBook2);
        bookDao.save(testBook3);

        SignUpDto signUpDto = new SignUpDto("test123", "password",
                "testfn", "testln", 21, "test123@gmail.com",
                "1990-12-09", "address123", "Admin");
        String signUpJson = mapper.writeValueAsString(signUpDto);

        String response = mvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andReturn().getResponse().getContentAsString();

        if (!response.equals("Email already exist."))
            testToken = response.substring(14, response.length() - 2);
    }

    @Nested
    @DisplayName("Get Books Tests")
    class GetBookTests {
        @Test
        public void testGetAllBooks_positive() throws Exception {
            testBook1.setBookId(1);
            testBook2.setBookId(2);
            testBook3.setBookId(3);

            List<Book> expectedBooks = new ArrayList<>();
            expectedBooks.add(testBook1);
            expectedBooks.add(testBook2);
            expectedBooks.add(testBook3);

            String expectedJson = mapper.writeValueAsString(expectedBooks);

            mvc.perform(get("/books"))
                    .andExpect(status().is(200))
                    .andExpect(content().json(expectedJson));
        }
    }

    @Nested
    @DisplayName("Add Book Tests")
    class AddBookTests {
        @Test
        public void testAddBookPositive() throws Exception {
            AddOrUpdateBookDTO newBook = new AddOrUpdateBookDTO("2425262728", "bookName4", "synopsis",
                    "author", 1, 1, 1996, "edition",
                    "publisher", true,
                    0.90, 10.99, "image");
            String jsonToSend = mapper.writeValueAsString(newBook);

            Book expectedBook = new Book("2425262728", "bookName4", "synopsis",
                    "author", fiction, 1, 1996, "edition",
                    "publisher", true,
                    0.90, 10.99, "image");
            expectedBook.setBookId(4);
            String expectedJson = mapper.writeValueAsString(expectedBook);

            System.out.println(mvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonToSend)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer" + testToken))
                    .andExpect(status().is(201))
                    .andExpect(content().json(expectedJson))
                    .andReturn().getResponse().getContentAsString());
        }
    }
    @Nested
    @DisplayName("Book Name is Empty")
    class CheckEmptyBookName {
        @Test
                public void TestEmptyBookNamePositive() throws Exception {
            AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "", "synopsis",
                    "author", 1, 1, 1996, "edition",
                    "publisher", true,
                    0.99, 10.99, "image");
            String jsonToSend = mapper.writeValueAsString(actualBook);

            System.out.println(mvc.perform(post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonToSend)
                    .header(HttpHeaders.AUTHORIZATION,"Bearer" + testToken))
                    .andExpect(status().is(400))
                    .andExpect(MockMvcResultMatchers.content().string("Book name cannot be blank.")));
        }
    }
    @Nested
    @DisplayName("Book synopsis is Empty")
    class CheckEmptySynopsis {
        @Test
            public void TestEmptySynopsisEmpty() throws Exception{
            AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("2425262728", "bookName5", "",
                    "author", 1, 1, 1996, "edition",
                    "publisher", true,
                    0.99, 10.99, "image");
            String jsonToSend = mapper.writeValueAsString(actualBook);
            System.out.println(mvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonToSend)
                            .header(HttpHeaders.AUTHORIZATION,"Bearer" + testToken))
                            .andExpect(status().is(400))
                            .andExpect(MockMvcResultMatchers.content().string("Synopsis cannot be blank.")));
        }
    }

    @Nested
    @DisplayName("Genre doesn't exist")
    class CheckGenreNotFound {
        @Test
            public  void TestGenreWasNotFound() throws Exception {

            AddOrUpdateBookDTO bookToAdd = new AddOrUpdateBookDTO("12345", "bookname5",
                    "synopsis", "author", 12345, 5, 1996, "edition",
                    "publisher", true, 0.5, 10.99, "image");

            String jsonToSend = mapper.writeValueAsString(bookToAdd);

            System.out.println(mvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonToSend)
                            .header(HttpHeaders.AUTHORIZATION,"Bearer" + testToken))
                    .andExpect(status().is(400))
                    .andExpect(MockMvcResultMatchers.content().string("Genre doesn't exist")));


        }
    }

    @Nested
    @DisplayName("Succesfully updated the book")
    class SuccessfullyUpdatedTheBook {
        @Test
            public void SuccesfullUpdatedTheBook() throws Exception{
            AddOrUpdateBookDTO bookToSend = new AddOrUpdateBookDTO("000001", "bookName", "synopsis",
                    "author", 1, 5, 1996, "edition",
                    "publisher", true,
                    0.5, 10.99, "image");
            String jsonToSend = mapper.writeValueAsString(bookToSend);
            Book actualBook = new Book("000001", "bookName", "synopsis",
                    "author", fiction, 5, 1996, "edition",
                    "publisher", true,
                    0.5, 10.99, "image");
            actualBook.setBookId(1);
            String expectedJson = mapper.writeValueAsString(actualBook);
            System.out.println(mvc.perform(put("/books/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonToSend)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer" + testToken))
                    .andExpect(status().is(200))
                    .andExpect(content().json(expectedJson))
                    .andReturn().getResponse().getContentAsString());

        }
    }

    @Nested
    @DisplayName("Unable to update the book")
        class BookUpdateWasUnsuccessful {
        @Test
            public void BookUpdateWasUncessful() throws Exception {
            AddOrUpdateBookDTO actualBook = new AddOrUpdateBookDTO("000001", "bookName", "synopsis",
                    "author", 1, 5, 1996, "edition",
                    "publisher", true,
                    0.5, 10.99, "image");
            String jsonToSend = mapper.writeValueAsString(actualBook);

            System.out.println(mvc.perform(put("/books/7")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonToSend)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer" + testToken))
                            .andExpect(MockMvcResultMatchers.status().is(400))
                            .andExpect(MockMvcResultMatchers.content().string("Book doesn't exist")));
        }
        }

    @Nested
    @DisplayName("Invalid Book Id")
    class InvalidBookId {
        @Test
        public void InvalidBookId() throws Exception {

            String jsonToSend = mapper.writeValueAsString(testBook1);

            System.out.println(mvc.perform(get("/books/-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonToSend)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer" + testToken))
                    .andExpect(MockMvcResultMatchers.status().is(400))
                    .andExpect(MockMvcResultMatchers.content().string("Book doesn't exist")));
        }
    }
}
