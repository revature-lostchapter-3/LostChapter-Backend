package com.revature.lostchapterbackend.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.lostchapterbackend.annotation.Customer;
import com.revature.lostchapterbackend.model.Checkout;
import com.revature.lostchapterbackend.model.Users;
import com.revature.lostchapterbackend.service.CheckoutService;
import com.revature.lostchapterbackend.utility.ValidateCheckoutUtil;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CheckoutController {

	@Autowired
	private HttpServletRequest req;
	
	@Autowired 
	private ValidateCheckoutUtil validateCheckoutUtil;
	
	@Autowired
	private CheckoutService cs;
	
	@Customer
	@PostMapping(path = "/user/checkout")
	public ResponseEntity<Object> payout(@RequestBody Checkout payout) {
		
		Users currentlyLoggedInUser = (Users) req.getSession().getAttribute("currentUser"); 
		// can be offloaded to frontend
		// main way to get User info such as Shipping Info, or should be manually inputed as well?
		try {
			validateCheckoutUtil.verifyCheckout(payout);
			
			//if everything checksout, send payout to service
			//then get cart by using currentlyLoggedInUser;
			
			return ResponseEntity.status(200).body(currentlyLoggedInUser);
		} catch (Exception e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}
			
			

		
		
	}
	
}