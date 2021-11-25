package com.cergy4.projetjavaav.controllers;


import com.cergy4.projetjavaav.models.Product;
import com.cergy4.projetjavaav.services.ProductsDao;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Date;
import java.util.List;


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


    @GetMapping("")
    public ResponseEntity<Object> listAll(){
        List<Product> list = productsDao.listAll();
        return ResponseEntity.ok(list);
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

    @GetMapping("/orders")
    public ResponseEntity<Object> orders(@RequestParam String range) {
        String[] splitted = range.split("-");

        int min = Integer.parseInt(splitted[0]);
        int max = Integer.parseInt(splitted[1]);
        int count = productsDao.count();
        int rangeSize = max - min + 1;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Range", min + "-" + max + "/" + count);
        responseHeaders.set("Accept-Range", "product " + count);
        responseHeaders.set("Link", "/products/orders?range=1-" + Math.min(rangeSize, count) + ";rel=\"first\", " +
                "product/orders?range=" + Math.max(min-rangeSize, 1) + "-" + Math.max(1, min-1) + ";rel=\"prev\", "+
                "product/orders?range=" + Math.min(max+1, count) + "-" + Math.min(max+rangeSize, count) + ";rel=\"next\", "+
                "product/orders?range=" + (count-rangeSize + 1) + "-" + count + ";rel=\"last\"");
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(productsDao.orders(min, max));
    }

}
