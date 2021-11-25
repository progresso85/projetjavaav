package com.cergy4.projetjavaav.filters;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumericFilter extends Filter {

    private final static Pattern RANGE_PATTERN = Pattern.compile("^\\(\\d*,\\d*\\)$");

    private boolean isRange;
    private int min, max;
    private List<Integer> values;

    public NumericFilter(String field, String value) {
        super(field);
        if (RANGE_PATTERN.matcher(value).find()) {
            isRange = true;
            String[] split = value.split(",");
            String minStr = split[0].substring(1);
            min = minStr.length() > 0 ? Integer.parseInt(minStr) : 0;
            String maxStr = split[1].substring(0, split[1].length() - 1);
            max = maxStr.length() > 0 ? Integer.parseInt(maxStr) : 10;
        }
        else {
            values = Arrays.stream(value.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        }
    }

    @Override
    public String buildSql() {
        if (isRange) {
            return String.format("%s BETWEEN ? and ?", field);
        }
        else {
            String[] params = new String[values.size()];
            Arrays.fill(params, "?");

            return String.format("%s in (%s)", field, String.join(", ", params));
        }
    }

    @Override
    public List<?> getParameters() {
        if (isRange)
            return List.of(min, max);
        else
            return values;
    }
}
