package com.cergy4.projetjavaav.services;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.cergy4.projetjavaav.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;

@Repository

public class ProductsDao {

    private final JdbcTemplate jdbcTemplate;

    public ProductsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void delete(int id) {
            String sql = "DELETE FROM products WHERE id = ?;";
            jdbcTemplate.update(sql, id);

    }


    public String checkValidity(Product product) {
        if (product.getCreatedAt() == null)
            return "missing field createdAt";
        if (product.getName() == null)
            return "missing field name";
        if (product.getRating() > 10 || product.getRating() < 0)
            return "rating must me between 0 and 10";
        if (product.getType() == null)
            return "missing field type";
        if (product.getCategoryId() == 0)
            return "missing field categoryId";

        return null;
    }

    public void add(Product product) {
        String sql = "INSERT INTO products (type, rating, name, createdAt, categoryId)" +
                "VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql,
                product.getType(), product.getRating(), product.getName(), product.getCreatedAt(), product.getCategoryId());

        Integer id = jdbcTemplate.query("SELECT LAST_INSERT_ID();",
                rs -> { rs.next(); return rs.getInt(1);});

        assert id != null;
        product.setId(id);


    }
}
