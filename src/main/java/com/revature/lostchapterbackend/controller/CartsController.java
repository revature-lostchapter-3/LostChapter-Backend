package com.revature.lostchapterbackend.controller;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.lostchapterbackend.annotation.Customer;
import com.revature.lostchapterbackend.exceptions.BookNotFoundException;
import com.revature.lostchapterbackend.exceptions.OutOfStockException;
import com.revature.lostchapterbackend.model.Carts;
import com.revature.lostchapterbackend.service.CartsService;


@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CartsController {

	@Autowired
	private CartsService cs;

	private final String PATTERN = "[0-9]+"; // checks String if it only contains numbers
	
	//@Customer
	@PostMapping(path = "/users/{userId}/cart") // Because a User is connected to a Cart, we can then find the cart Id
												// // by using the User.
	public ResponseEntity<Object> addBookToCart(@PathVariable("userId") String userId,
			@RequestParam("bookId") String bookId, @RequestParam("quantityToBuy") String quantityToBuy) throws BookNotFoundException {
		// Aspect or another class for protecting endpoint
		try {
			Carts currentCart = null;
			if (userId.matches(PATTERN) && bookId.matches(PATTERN) && quantityToBuy.matches(PATTERN)) {
				currentCart = cs.addBooksToCart(currentCart, userId, bookId, quantityToBuy);
				currentCart.getUser().setPassword("");
				return ResponseEntity.status(200).body(currentCart);
			} else {
				throw new NumberFormatException("product id or quantity must be of type int!");
			}
		} catch (NumberFormatException | OutOfStockException | InvalidParameterException | NoSuchElementException | BookNotFoundException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (NoResultException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} 
	}

	//@Customer
	@GetMapping(path = "/users/{userId}/cart") // endpoint used for displaying all Books in the Cart
	public ResponseEntity<Object> getCartById(@PathVariable("userId") String userId) {
		// Aspect or another class for protecting endpoint
		try {

			Carts getCartById = cs.getCartById(userId);
			return ResponseEntity.status(200).body(getCartById);

		} catch (InvalidParameterException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}  catch (NoSuchElementException e) {
			return ResponseEntity.status(404).body("There is no cart with the id of " +userId);
		}
	}

	//@Customer
	@DeleteMapping(path = "/users/{userId}/cart")
	public ResponseEntity<Object> delteteProductInCart(@PathVariable("userId") String cartId,
			@RequestParam(name = "bookId", required = false) String bookId) throws BookNotFoundException, NoResultException {

		try {
			Carts currentCart = null;
			if (bookId != null && (cartId.matches(PATTERN) && bookId.matches(PATTERN))) {
				currentCart = cs.delteteProductInCart(currentCart, cartId, bookId);
				return ResponseEntity.status(200).body(currentCart);
			} else if (bookId == null) {
				currentCart = cs.delteteAllProductInCart(currentCart, cartId);
				return ResponseEntity.status(200).body(currentCart);
			} else {
				throw new NumberFormatException("cart id/product id must be of type int!");
			}
		} catch (NumberFormatException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (NoResultException | BookNotFoundException | NoSuchElementException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} 
	}
}
