package com.revature.lostchapterbackend.controller;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

import javax.persistence.NoResultException;

import com.revature.lostchapterbackend.annotation.Customer;
import com.revature.lostchapterbackend.exceptions.BookNotFoundException;
import com.revature.lostchapterbackend.exceptions.OutOfStockException;
import com.revature.lostchapterbackend.model.Wishlist;
import com.revature.lostchapterbackend.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    private final String PATTERN = "[0-9]+"; // checks String if it only contains numbers


    @Customer
    @PostMapping(path = "/user/{userId}/wishlist")
    public ResponseEntity<Object> addBookToWishlist(
            @PathVariable("userId") String userId,
            @RequestParam("bookId") String bookId,
            @RequestParam("quantityToBuy") String quantityToBuy
        )throws BookNotFoundException {
    try {
        Wishlist currentWishlist = null;
        if (userId.matches(PATTERN) && bookId.matches(PATTERN)) {
            currentWishlist = wishlistService.addBooksToWishlist(currentWishlist, userId, bookId, quantityToBuy);
            return ResponseEntity.status(200).body(currentWishlist);
        } else {
            throw new NumberFormatException("product id or quantity must be of type int!");
        }
    }catch (NumberFormatException | OutOfStockException | InvalidParameterException | NoSuchElementException | BookNotFoundException e)
        {
            return ResponseEntity.status(400).body(e.getMessage());
    }catch (NoResultException e ){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Customer
    @GetMapping(path = "/users/{userId}/wishlist") // endpoint used for displaying all Books in the Wishlist
    public ResponseEntity<Object> getWishListById(@PathVariable("userId") String userId) {
        // Aspect or another class for protecting endpoint
        try {
            Wishlist getWishlistByID = wishlistService.getWishlistById(userId);
            return ResponseEntity.status(200).body(getWishlistByID);

        } catch (InvalidParameterException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }  catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("There is no wishlist with the id of " +userId);
        }
    }

    @Customer
    @DeleteMapping(path = "/user/{wishlistId}/wishlist/{productId}")
    public ResponseEntity<Object> deleteProductInWishlist(
            @PathVariable("currentWishlist") Wishlist currentWishlist,
            @PathVariable("wishlistId") String wishlistId,
            @PathVariable("productId") String productId) {
        try {
            wishlistService.deleteProductInWishlist(currentWishlist, wishlistId, productId);
            return ResponseEntity.status(200).body("Book successfully deleted from wishlist");
        } catch (InvalidParameterException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (NoSuchElementException | BookNotFoundException e) {
            return ResponseEntity.status(404).body("There is no wishlist with the id of " + wishlistId);
        }
    }

    @Customer
    @DeleteMapping(path = "/user/{wishlistId}/wishlist")
    public ResponseEntity<Object> deleteAllProductInWishlist(
            @PathVariable("currentWishlist") Wishlist currentWishlist,
            @PathVariable("wishlistId") String wishlistId) {
        try {
            wishlistService.deleteAllProductInWishlist(currentWishlist, wishlistId);
            return ResponseEntity.status(200).body("Wishlist successfully deleted");
        } catch (InvalidParameterException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("There is no wishlist with the id of " + wishlistId);
        }
    }
}
