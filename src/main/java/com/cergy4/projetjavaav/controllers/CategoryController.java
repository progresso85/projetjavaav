package com.cergy4.projetjavaav.controllers;

import com.cergy4.projetjavaav.models.Category;
import com.cergy4.projetjavaav.services.CategoryDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryDao categoryDao;

    public CategoryController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @GetMapping("")
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categories = categoryDao.getAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable int id) {
        Category category = categoryDao.get(id);

        if (category == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(category);
    }

    @PostMapping("")
    public ResponseEntity<Object> post(@RequestBody Category category) {
        String error = categoryDao.checkValidity(category);
        if (error != null)
            return ResponseEntity.badRequest().body(error);

        categoryDao.add(category);
        return ResponseEntity.created(URI.create("/categories/"+ category.getId())).body(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        boolean result = categoryDao.delete(id);
        if (result)
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.notFound().build();
    }


}
