package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Payment;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public PaymentController(PaymentRepository paymentRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> payload) {
        Long bookId = Long.valueOf(payload.get("bookId").toString());
        Long userId = Long.valueOf(payload.get("userId").toString());
        String method = (String) payload.getOrDefault("paymentMethod", "UPI / Card");

        Book book = bookRepository.findById(bookId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (book == null || user == null) {
            return ResponseEntity.badRequest().body("Invalid Book or User");
        }
        if (!"AVAILABLE".equalsIgnoreCase(book.getStatus())) {
            return ResponseEntity.badRequest().body("Book is no longer available.");
        }

        Payment payment = new Payment();
        payment.setBook(book);
        payment.setUser(user);
        payment.setAmount(book.getPrice() != null ? book.getPrice() : 0.0);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus("COMPLETED");
        payment.setTimestamp(LocalDateTime.now());

        book.setStatus("SOLD");
        bookRepository.save(book);

        Payment saved = paymentRepository.save(payment);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/{userId}")
    public List<Payment> getUserPayments(@PathVariable Long userId) {
        return paymentRepository.findByUserId(userId);
    }
}