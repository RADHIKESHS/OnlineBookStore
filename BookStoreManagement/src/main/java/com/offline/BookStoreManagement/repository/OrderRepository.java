package com.offline.BookStoreManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.offline.BookStoreManagement.model.Order;
import com.offline.BookStoreManagement.model.Users;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(Users user);
}

