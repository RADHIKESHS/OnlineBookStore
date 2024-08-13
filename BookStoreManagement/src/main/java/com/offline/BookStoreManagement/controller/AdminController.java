package com.offline.BookStoreManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.offline.BookStoreManagement.model.Order;
import com.offline.BookStoreManagement.model.Users;
import com.offline.BookStoreManagement.service.OrderService;
import com.offline.BookStoreManagement.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;

    @GetMapping("/users/{username}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable String username) {
        Users user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    @PutMapping("/activity/{id}")
    public ResponseEntity<Users> updateUserActivity(@PathVariable Long id, @Valid @RequestParam String activity ) {
    	Users u= userService.findByUserId(id).get();
    	u.setActivity(activity);
        Users updatedUser = userService.updateUser(id, u);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
    
	@GetMapping("/order/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
	    Order order = orderService.getOrderById(id);
	    return new ResponseEntity<>(order, HttpStatus.OK);
	}
	
	@GetMapping("/allOrders/{userId}")
	public ResponseEntity<List<Order>> getUserOrders(@RequestParam Long userId) {
	    List<Order> orders = orderService.getUserOrders(userId);
	    return new ResponseEntity<>(orders, HttpStatus.OK);
	}

}

