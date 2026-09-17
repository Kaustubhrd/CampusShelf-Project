package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Payment;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PaymentRepository paymentRepository;

    public AdminController(UserRepository userRepository, BookRepository bookRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User removed");
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.ok("Book deleted by Admin");
    }

    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}