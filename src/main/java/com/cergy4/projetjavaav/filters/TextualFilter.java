package com.cergy4.projetjavaav.filters;


import java.util.Arrays;
import java.util.List;

public class TextualFilter extends Filter {

    private final String[] values;

    public TextualFilter(String field, String value) {
        super(field);
        this.values = value.split(",");
    }

    @Override
    public String buildSql() {
        String[] params = new String[values.length];
        Arrays.fill(params, "?");

        return String.format("%s in (%s)", field, String.join(", ", params));
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(values);
    }


}
