package com.cergy4.projetjavaav.controllers;


import com.cergy4.projetjavaav.filters.DateFilter;
import com.cergy4.projetjavaav.filters.Filter;
import com.cergy4.projetjavaav.filters.NumericFilter;
import com.cergy4.projetjavaav.filters.TextualFilter;
import com.cergy4.projetjavaav.models.Product;
import com.cergy4.projetjavaav.services.ProductsDao;
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
    public ResponseEntity<Object> listAll(@RequestParam Optional<String> rating, @RequestParam Optional<String> type,
                                          @RequestParam Optional<String> createdat, @RequestParam Optional<String> categoryid){
        List<Filter> filters = new ArrayList<>();
        rating.ifPresent(s -> filters.add(new NumericFilter("rating", s)));
        type.ifPresent(s -> filters.add(new TextualFilter("type", s)));
        createdat.ifPresent(s -> filters.add(new DateFilter("createdAt", s)));
        categoryid.ifPresent(s -> filters.add(new NumericFilter("categoryId", s)));
        List<Product> list = productsDao.listAll(filters);
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

    /* @RestController
    @RequestMapping("/orders")
    public class EmployeeController
    {
        @GetMapping
        public ResponseEntity<List<Product>> getAllEmployees(
                @RequestParam(defaultValue = "0") Integer pageNo,
                @RequestParam(defaultValue = "1") Integer pageSize,
                @RequestParam(defaultValue = "id") String sortBy)
        {
            List<Product> list = service.getAllEmployees(pageNo, pageSize, sortBy);

            return new ResponseEntity<List<Product>>(list, new HttpHeaders(), HttpStatus.OK);
        }
    }*/
}
