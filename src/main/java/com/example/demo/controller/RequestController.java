package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.BookRequest;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.BookRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class RequestController {

    private final BookRequestRepository requestRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public RequestController(BookRequestRepository requestRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> applyBorrowRequest(@RequestBody Map<String, Long> payload) {
        Long bookId = payload.get("bookId");
        Long requesterId = payload.get("requesterId");

        Book book = bookRepository.findById(bookId).orElse(null);
        User requester = userRepository.findById(requesterId).orElse(null);

        if (book == null || !"AVAILABLE".equalsIgnoreCase(book.getStatus())) {
            return ResponseEntity.badRequest().body("Book is not available for request.");
        }
        if (requester == null) {
            return ResponseEntity.badRequest().body("Invalid requester.");
        }
        if (book.getOwner().getId().equals(requesterId)) {
            return ResponseEntity.badRequest().body("You cannot request your own book.");
        }

        BookRequest request = new BookRequest();
        request.setBook(book);
        request.setRequester(requester);
        request.setStatus("PENDING");

        BookRequest saved = requestRepository.save(request);
        System.out.println("[Notification Service] Email/WhatsApp alert sent to owner: " + book.getOwner().getEmail());

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/owner/{ownerId}")
    public List<BookRequest> getIncomingRequests(@PathVariable Long ownerId) {
        return requestRepository.findByBookOwnerId(ownerId);
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long requestId, @RequestParam String action) {
        BookRequest req = requestRepository.findById(requestId).orElse(null);
        if (req == null) {
            return ResponseEntity.notFound().build();
        }

        if ("ACCEPT".equalsIgnoreCase(action)) {
            req.setStatus("ACCEPTED");
            Book book = req.getBook();
            book.setStatus("ISSUED");
            bookRepository.save(book);
            System.out.println("[Notification Service] Confirmation sent to borrower: " + req.getRequester().getEmail());
        } else {
            req.setStatus("REJECTED");
        }

        return ResponseEntity.ok(requestRepository.save(req));
    }
}