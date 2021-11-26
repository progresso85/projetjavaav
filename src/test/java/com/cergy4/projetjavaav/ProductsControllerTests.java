package com.cergy4.projetjavaav;

import com.cergy4.projetjavaav.controllers.ProductsController;
import com.cergy4.projetjavaav.models.Product;
import com.cergy4.projetjavaav.services.ProductsDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductsController.class)
public class ProductsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductsDao productsDao;

    @Test
    public void testGetProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProductsById() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setName("Test");

        Mockito.when(productsDao.readById(1)).thenReturn(product);
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test")));
    }

    @Test
    public void testCreatedProducts() throws Exception{
        Product product = new Product();
        product.setId(2);
        product.setName("Test2");
        product.setType("TypeTest");
        product.setRating((byte) 1);
        product.setCategoryId(1);


        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product)))


                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test2")));
    }

}

