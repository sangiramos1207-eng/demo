package com.example.demo.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    // RF4 lista por categoria / RF6 buscar
    @GetMapping
    public List<Product> list(@RequestParam(required = false) String category,
                              @RequestParam(required = false) String q) {

        if (q != null && !q.isBlank()) {
            return repo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q);
        }

        if (category != null && !category.isBlank()) {
            return repo.findByCategory_NameIgnoreCase(category);
        }

        return repo.findAll();
    }

    // RF5 detalle
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        var opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    // ===== CRUD (solo ADMIN por tu SecurityConfig) =====

    // POST /api/products
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Product product) {
        // No usamos product.setId(null) porque tu entidad no tiene setter
        Product saved = repo.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Product body) {
        var opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        Product existing = opt.get();
        existing.setName(body.getName());
        existing.setDescription(body.getDescription());
        existing.setImageUrl(body.getImageUrl());
        existing.setPrice(body.getPrice());
        existing.setCategory(body.getCategory()); // para cambiar categoría

        Product saved = repo.save(existing);
        return ResponseEntity.ok(saved);
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}