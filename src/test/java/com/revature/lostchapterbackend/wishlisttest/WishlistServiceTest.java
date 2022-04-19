package com.revature.lostchapterbackend.wishlisttest;

import com.revature.lostchapterbackend.dao.BookDAO;
import com.revature.lostchapterbackend.dao.BookToBuyDAO;
import com.revature.lostchapterbackend.dao.WishlistDAO;
import com.revature.lostchapterbackend.service.BookService;
import com.revature.lostchapterbackend.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class WishlistServiceTest {

    private WishlistService wls;
    private WishlistDAO wld;
    private BookToBuyDAO btbd;

    private BookService bs;
    private BookDAO bd;

    @BeforeEach
    public void setup() {
        this.bd = mock(BookDAO.class);
        this.bs = new BookService(bd);
        this.wld = mock(WishlistDAO.class);
        this.btbd = mock(BookToBuyDAO.class);
        this.wls = new WishlistService(bs, wld, btbd);
    }

    @Test
    void addBooksToWishlist() {
        // TODO: Test that the book is added to the wishlist
    }

    @Test
    void addBooksToWishlist_NoSuchElementException_NegativeTest() {
    // TODO: Test that no such book exists, so it can't be added to wishlist
    }

    @Test
    void deleteBookInTheWishlist_PositiveTest() {
    // TODO: Test that the book is deleted from the wishlist
    }

    @Test
    void deleteAllBooksInTheWishlist_PositiveTest() {
    // TODO: Test that all books are deleted from the wishlist
    }
}
