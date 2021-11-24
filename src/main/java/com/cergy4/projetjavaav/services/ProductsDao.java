package com.cergy4.projetjavaav.services;

import com.cergy4.projetjavaav.models.Product;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class ProductsDao {

    private JdbcTemplate jdbcTemplate;

    public ProductsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    public void delete(int id) {
            String sql = "DELETE FROM products WHERE id = ?;";
            jdbcTemplate.update(sql, id);
    }


    public String checkValidity(Product product) {
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

    public List<Product> listAll() {
        String sql = "SELECT * FROM Products;";
        List<Product> list = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Product.class));
        return list;
    }

    public Product update(int id, Product product) {
        String sql = "UPDATE products SET name = ?, rating = ?, type = ?, categoryId = ? " +
                "WHERE id = ?;";

        int result = jdbcTemplate.update(sql,
                product.getName(), product.getRating(), product.getType(), product.getCategoryId(), id);

        if (result != 1) {
            return null;
        }

        return jdbcTemplate.query("SELECT * FROM products WHERE id = ?", BeanPropertyRowMapper.newInstance(Product.class),
                id).get(0);
    }

    public Product readById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?;";
        List<Product> Products = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Product.class), id);

        return Products.get(0);


    }
}

