package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_NameIgnoreCase(String categoryName);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String q1, String q2);
}