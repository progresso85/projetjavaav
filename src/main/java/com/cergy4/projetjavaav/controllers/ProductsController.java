package com.cergy4.projetjavaav.controllers;


import com.cergy4.projetjavaav.models.Product;
import com.cergy4.projetjavaav.services.ProductsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.Date;


@Controller
@RequestMapping("/products")
public class ProductsController {

    private final ProductsDao productsDao;

    public ProductsController(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProducts(@PathVariable int id) {
        productsDao.delete(id);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable int id){
        Product product = productsDao.readById(id);

        return ResponseEntity.ok(product);

    }

}
