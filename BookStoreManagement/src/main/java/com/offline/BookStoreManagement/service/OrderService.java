package com.offline.BookStoreManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offline.BookStoreManagement.exceptions.DatabaseException;
import com.offline.BookStoreManagement.exceptions.InvalidInputException;
import com.offline.BookStoreManagement.exceptions.ResourceNotFoundException;
import com.offline.BookStoreManagement.model.Book;
import com.offline.BookStoreManagement.model.Order;
import com.offline.BookStoreManagement.model.OrderStatus;
import com.offline.BookStoreManagement.model.Users;
import com.offline.BookStoreManagement.repository.BookRepository;
import com.offline.BookStoreManagement.repository.OrderRepository;
import com.offline.BookStoreManagement.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public Order placeOrder(Long userId, Long bookId, Integer quantity) {
    	Users user= userRepository.findById(userId)
    			.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        if (book.getQuantity() < quantity) {
            throw new InvalidInputException("Insufficient stock for book id: " + bookId);
        }

        book.setQuantity(book.getQuantity() - quantity);
        try {
            Order order = new Order();
            order.setUser(user);
            order.setBook(book);
            order.setQuantity(quantity);
            order.setTotalPrice(book.getPrice() * quantity);
            order.setStatus(OrderStatus.PLACED);
            order.setOrderDate(LocalDateTime.now());
            return orderRepository.save(order);
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to place order: " + e.getMessage());
        }
    }

    public List<Order> getUserOrders(Long userId) {
    	Users user= userRepository.findById(userId)
    			.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}


