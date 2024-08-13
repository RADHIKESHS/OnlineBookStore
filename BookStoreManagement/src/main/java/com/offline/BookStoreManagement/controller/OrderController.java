package com.offline.BookStoreManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.offline.BookStoreManagement.model.Order;
import com.offline.BookStoreManagement.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestParam Long userId, @RequestParam Long bookId, @RequestParam Integer quantity) {
        Order order = orderService.placeOrder(userId, bookId, quantity);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
}

