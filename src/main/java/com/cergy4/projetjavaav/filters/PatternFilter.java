package com.cergy4.projetjavaav.filters;

import java.util.List;

public class PatternFilter extends Filter {

    String value;

    public PatternFilter(String field, String value) {
        super(field);
        this.value = value;
    }

    @Override
    public String buildSql() {
        return String.format("%s LIKE ?", field);
    }

    @Override
    public List<?> getParameters() {
        return List.of('%' + value + '%');
    }
}
