package com.example.demo.repository;

import com.example.demo.model.BookRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
    List<BookRequest> findByBookOwnerId(Long ownerId);
    List<BookRequest> findByRequesterId(Long requesterId);
}