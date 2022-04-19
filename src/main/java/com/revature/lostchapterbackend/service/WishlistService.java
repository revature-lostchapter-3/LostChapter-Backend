package com.revature.lostchapterbackend.service;

import com.revature.lostchapterbackend.dao.BookToBuyDAO;
import com.revature.lostchapterbackend.dao.CartsDAO;
import com.revature.lostchapterbackend.dao.WishlistDAO;
import com.revature.lostchapterbackend.exceptions.BookNotFoundException;
import com.revature.lostchapterbackend.exceptions.OutOfStockException;
import com.revature.lostchapterbackend.model.Book;
import com.revature.lostchapterbackend.model.BookToBuy;
import com.revature.lostchapterbackend.model.Carts;
import com.revature.lostchapterbackend.model.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

@Service
public class WishlistService {
    public static final int QUANTITY_TO_BUY = 1;
    /**
     * To-do list:
     * - Create CRUD (create- read - update - delete)
     * - Go back to the WishlistDAO to create paging and sorting and then implements them in this class
     */

    @Autowired
    private BookService bs;

    @Autowired
    private CartsDAO cd; // using JPA Repository

    @Autowired
    private BookToBuyDAO btbd;

    @Autowired
    private WishlistDAO wld;

//    public WishlistService(BookService bs, BookToBuyDAO btbd, WishlistDAO wld) {
//        this.bs = bs;
//        this.btbd = btbd;
//        this.wld = wld;
//    }

    public WishlistService(BookService bs, WishlistDAO wld, BookToBuyDAO btbd) {
        this.bs = bs;
        this.wld = wld;
        this.btbd = btbd;
    }

    public Wishlist getWishlistById(String id) {
        try {

            int wishlistId = Integer.parseInt(id);
            return wld.findById(wishlistId).get();
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("The Id entered must be an int.");
        }
    }

    public Wishlist addBooksToWishlist(Wishlist currentWishlist, String userId, String bookId, String quantityToBuy)
            throws OutOfStockException, BookNotFoundException {

        currentWishlist = this.getWishlistById(userId);
        //Retrieve a book obj from id from the bookService class
        Book b = bs.getBookById(bookId);
        //We will get an exception telling us if the book is out of stock
        if (b.getQuantity() <= 0) {
            throw new OutOfStockException("Currently Out of Stock...");
        }
        //If the book is available we pass it to BookToBuy
        BookToBuy booksToBeBought = new BookToBuy(b);
        //Since this is a wish list, I put default number as 1 book
        booksToBeBought.setQuantityToBuy(QUANTITY_TO_BUY);
        List<BookToBuy> currentBookInWishList = currentWishlist.getBooksToBuy();
        currentBookInWishList.add(booksToBeBought);
        btbd.save(booksToBeBought);
        return wld.save(currentWishlist);
    }

    public boolean checkBookInTheWishList(List<BookToBuy> currentBooksInTheWishList, Book b) {
        boolean result = false;
        for (BookToBuy b1 : currentBooksInTheWishList) {
            if (b1.getBooks() == b) {
                result = true;
            }
        }
        return result;
    }

    public Wishlist deleteProductInWishlist(Wishlist currentWishlist, String wishlistId, String productId) throws BookNotFoundException {
        currentWishlist = this.getWishlistById(wishlistId);

        int prodId = Integer.parseInt(productId);
        List<BookToBuy> currentBooksInTheWishlist = currentWishlist.getBooksToBuy();
        int quantityToDelete = 0;
        try{
            Iterator<BookToBuy> iter = currentBooksInTheWishlist.iterator();
            System.out.println(currentBooksInTheWishlist);
            BookToBuy b1 = null;
            while (iter.hasNext()){
                b1 = iter.next();
                if (b1.getBooks().getBookId() == prodId){
                    iter.remove();
                    quantityToDelete = b1.getId();
                }
            }
            currentWishlist.setBooksToBuy(currentBooksInTheWishlist);
            btbd.deleteById(quantityToDelete);
        }catch (EmptyResultDataAccessException e){
            throw new BookNotFoundException("Product not found on this wishlist");
        }
        return wld.save(currentWishlist);
    }

    public Wishlist deleteAllProductInWishlist(Wishlist currentWishlist, String wishlistId) {
        currentWishlist = this.getWishlistById(wishlistId);

        List<BookToBuy> currentBooksInTheWishlist = currentWishlist.getBooksToBuy();

        Iterator<BookToBuy> iter = currentBooksInTheWishlist.iterator();
        System.out.println(currentBooksInTheWishlist);
        BookToBuy b1 = null;
        while (iter.hasNext()) {
            b1 = iter.next();
            iter.remove();
            currentWishlist.setBooksToBuy(currentBooksInTheWishlist);
            System.out.println(b1.getId());
            btbd.deleteById(b1.getId());
        }


        return wld.save(currentWishlist);
    }

}
