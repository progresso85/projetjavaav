package com.cergy4.projetjavaav.controllers;


import com.cergy4.projetjavaav.filters.*;
import com.cergy4.projetjavaav.models.Product;
import com.cergy4.projetjavaav.services.ProductsDao;

import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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
    public ResponseEntity<Object> listAll(@RequestParam Optional<String> name,
                                          @RequestParam Optional<String> rating, @RequestParam Optional<String> type,
                                          @RequestParam Optional<String> createdat, @RequestParam Optional<String> categoryid,
                                          @RequestParam Optional<String> asc,  @RequestParam Optional<String> desc){
        List<String> sortAsc = new ArrayList<>();
        List<String> sortDesc = new ArrayList<>();
        asc.ifPresent(s -> sortAsc.addAll(List.of(s.split(","))));
        desc.ifPresent(s -> sortDesc.addAll(List.of(s.split(","))));

        List<Filter> filters = new ArrayList<>();
        name.ifPresent(s -> filters.add(new TextualFilter("name", s)));
        handleFilters(filters, rating, type, createdat, categoryid);

        List<Product> list = productsDao.listAll(filters, sortAsc, sortDesc);
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
                .body(productsDao.pagination(min, max));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String name,
                                         @RequestParam Optional<String> rating, @RequestParam Optional<String> type,
                                         @RequestParam Optional<String> createdat, @RequestParam Optional<String> categoryid) {
        List<Filter> filters = new ArrayList<>();
        filters.add(new PatternFilter("name", name));
        handleFilters(filters, rating, type, createdat, categoryid);

        List<Product> list = productsDao.listAll(filters, new ArrayList<>(), new ArrayList<>());
        return ResponseEntity.ok(list);
    }

    private void handleFilters(List<Filter> filters, @RequestParam Optional<String> rating, @RequestParam Optional<String> type, @RequestParam Optional<String> createdat, @RequestParam Optional<String> categoryid) {
        rating.ifPresent(s -> filters.add(new NumericFilter("rating", s)));
        type.ifPresent(s -> filters.add(new TextualFilter("type", s)));
        createdat.ifPresent(s -> filters.add(new DateFilter("createdAt", s)));
        categoryid.ifPresent(s -> filters.add(new NumericFilter("categoryId", s)));
    }

}
