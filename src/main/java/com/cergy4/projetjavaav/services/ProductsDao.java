package com.cergy4.projetjavaav.services;


import com.cergy4.projetjavaav.filters.Filter;
import com.cergy4.projetjavaav.models.Product;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean isValidField(String value){
        String[] validField = {"id","type","rating","name","createdat","categoryid"};
        return Arrays.asList(validField).contains(value);
    }

    public List<Product> listAll(List<Filter> filters,List<String> sortAsc, List<String> sortDesc) {
        String sql = "SELECT * FROM products ";
        if (!filters.isEmpty()) {
            sql += " WHERE ";
            sql += String.join(" AND ", filters.stream().map(Filter::buildSql).toArray(String[]::new));
        }


        List<Object> parameters = new ArrayList<>();
        for (Filter filter : filters)
            parameters.addAll(filter.getParameters());

        sortAsc = sortAsc.stream().filter(this::isValidField).collect(Collectors.toList());
        sortDesc = sortDesc.stream().filter(this::isValidField).collect(Collectors.toList());

        List<String> sorts = new ArrayList<>();
        for (String item : sortAsc) {
            sorts.add(item + " ASC " );
        }

        for (String item : sortDesc) {
            sorts.add(item + " DESC " );
        }

        if(!sorts.isEmpty()){
            sql += " ORDER BY ";
            sql += String.join(" , ", sorts);
        }
        
        String finalSql = sql;
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(finalSql);
            for (int i = parameters.size() - 1; i >= 0; i--) {
                ps.setObject(i+1, parameters.get(i));
            }
            return ps;
        }, BeanPropertyRowMapper.newInstance(Product.class));
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
        List<Product> products = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Product.class), id);

        return products.get(0);
    }

    public List<Product> pagination(int min, int max) {
        String sql = "SELECT * FROM products LIMIT ?,?";
        List<Product> list = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Product.class), min-1, max-min+1);
        return list;
    }

    public int count(){
        String sql = "SELECT COUNT(*) FROM products";
       Integer count = jdbcTemplate.query(sql, rs -> { rs.next(); return rs.getInt(1);});
       assert count != null;
        return count;
    }
}

