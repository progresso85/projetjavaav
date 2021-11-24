package com.cergy4.projetjavaav.controllers;

import com.cergy4.projetjavaav.services.ProductsDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductsController {

    private final ProductsDao productsDao;

    public ProductsController(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }
}
