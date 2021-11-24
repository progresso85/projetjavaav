package com.cergy4.projetjavaav.services;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.cergy4.projetjavaav.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class ProductsDao {


    @Autowired
    private JdbcTemplate jdbcTemplate;



    public void delete(int id) {
            String sql = "DELETE FROM products WHERE id = ?;";
            jdbcTemplate.update(sql, id);



    }
}
