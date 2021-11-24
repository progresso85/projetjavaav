package com.cergy4.projetjavaav.controllers;


import com.cergy4.projetjavaav.services.ProductsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsDao productsDao;

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProducts(@PathVariable int id) {
        productsDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    public ProductsController(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

}
