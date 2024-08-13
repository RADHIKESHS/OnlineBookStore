package com.offline.BookStoreManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offline.BookStoreManagement.exceptions.DatabaseException;
import com.offline.BookStoreManagement.exceptions.InvalidInputException;
import com.offline.BookStoreManagement.exceptions.ResourceNotFoundException;
import com.offline.BookStoreManagement.model.Book;
import com.offline.BookStoreManagement.repository.BookRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        try {
            return bookRepository.findAll();
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve books: " + e.getMessage());
        }
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }
    
    public List<Book> searchBooks(String searchTerm) {
        return bookRepository.searchBooks(searchTerm);
    }

    public Book addBook(@Valid Book book) {
        try {
            return bookRepository.save(book);
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to save book: " + e.getMessage());
        }
    }

    public Book updateBook(Long id, @Valid Book book) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setPrice(book.getPrice());
        existingBook.setQuantity(book.getQuantity());
        try {
            return bookRepository.save(existingBook);
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to update book: " + e.getMessage());
        }
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        try {
            bookRepository.delete(book);
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete book: " + e.getMessage());
        }
    }
}
