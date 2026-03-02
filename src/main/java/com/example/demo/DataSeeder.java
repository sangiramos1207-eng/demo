package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repo.CategoryRepository;
import com.example.demo.repo.ProductRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository catRepo;
    private final ProductRepository prodRepo;

    public DataSeeder(CategoryRepository catRepo, ProductRepository prodRepo) {
        this.catRepo = catRepo;
        this.prodRepo = prodRepo;
    }

    @Override
    public void run(String... args) {
        if (catRepo.count() > 0) return;

        Category hamburguesas = catRepo.save(new Category("hamburguesas"));
        Category bebidas = catRepo.save(new Category("bebidas"));
        Category postres = catRepo.save(new Category("postres"));

        prodRepo.save(new Product("Classic Burger","Hamburguesa clásica","",12.5, hamburguesas));
        prodRepo.save(new Product("Coca Cola","Bebida gaseosa","",3.0, bebidas));
        prodRepo.save(new Product("Brownie","Postre de chocolate","",5.5, postres));
    }
}