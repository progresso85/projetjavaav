package com.cergy4.projetjavaav.controllers;

import com.cergy4.projetjavaav.models.Product;
import com.cergy4.projetjavaav.services.ProductsDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.Date;

@Controller
@RequestMapping("/products")
public class ProductsController {

    private final ProductsDao productsDao;

    public ProductsController(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

    @PostMapping("")
    public ResponseEntity<Object> postProduct(@RequestBody Product product) {
        product.setCreatedAt(new Date());

        String error = productsDao.checkValidity(product);
        if (error != null)
            return ResponseEntity.badRequest().body(error);

        productsDao.add(product);

        return ResponseEntity.created(URI.create("/products/"+ product.getId())).body(product);
    }

}
