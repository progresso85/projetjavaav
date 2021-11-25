package com.cergy4.projetjavaav.filters;

import java.util.List;

public abstract class Filter {

    protected String field;

    protected Filter(String field) {
        this.field = field;
    }

    public abstract String buildSql();

    public abstract List<?> getParameters();

}
