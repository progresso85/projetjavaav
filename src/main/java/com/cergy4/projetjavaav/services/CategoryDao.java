package com.cergy4.projetjavaav.services;

import com.cergy4.projetjavaav.models.Category;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    public CategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String checkValidity(Category category) {
        if (category.getName() == null)
            return "missing field name";

        return null;
    }


    public List<Category> getAll() {
        return jdbcTemplate.query("SELECT * FROM categories", BeanPropertyRowMapper.newInstance(Category.class));
    }

    public Category get(int id) {
        List<Category> query = jdbcTemplate.query("SELECT * FROM categories WHERE id = ?",
                BeanPropertyRowMapper.newInstance(Category.class), id);
        if (query.isEmpty())
            return null;
        return query.get(0);
    }


    public void add(Category category) {
        String sql = "INSERT INTO categories (name)" +
                "VALUES (?);";
        jdbcTemplate.update(sql,
                category.getName());

        Integer id = jdbcTemplate.query("SELECT LAST_INSERT_ID();",
                rs -> { rs.next(); return rs.getInt(1);});

        assert id != null;
        category.setId(id);
    }

    public boolean delete(int id) {
        int result = jdbcTemplate.update("DELETE FROM categories WHERE id = ?", id);
        return result == 1;
    }
}
