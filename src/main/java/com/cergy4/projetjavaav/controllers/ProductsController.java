package com.cergy4.projetjavaav.controllers;


import com.cergy4.projetjavaav.models.Product;
import com.cergy4.projetjavaav.services.ProductsDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/products")
public class ProductsController {

    private final ProductsDao productsDao;
    private ProductsDao ProductDao;

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

    //@GetMapping("")
    @GetMapping(value = "", method = RequestMethod.GET)
    public String index(Model model){
        List<Product> list = ProductDao.listAll();
        model.addAttribute("products", list);
        return "/products/";
    }

   @PutMapping("/{id}")
   public ResponseEntity<Object> updateProduct(@PathVariable int id, @RequestBody Product product) {
       String error = productsDao.checkValidity(product);
       if (error != null)
           return ResponseEntity.badRequest().body(error);

       Product updatedProduct = productsDao.update(id, product);

       if (updatedProduct == null) {
           return ResponseEntity.notFound().build();
       }
       else {
           return ResponseEntity.ok(updatedProduct);
       }
   }
}
