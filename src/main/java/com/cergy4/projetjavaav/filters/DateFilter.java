package com.cergy4.projetjavaav.filters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DateFilter extends Filter {

    private final static Pattern RANGE_PATTERN = Pattern.compile("^\\((\\d{1,2}-\\d{1,2}-\\d{4})?,(\\d{1,2}-\\d{1,2}-\\d{4})?\\)$");

    private boolean isRange;
    private Date min, max;
    private List<Date> values;

    public DateFilter(String field, String value) {
        super(field);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (RANGE_PATTERN.matcher(value).find()) {
            isRange = true;
            String[] split = value.split(",");

            try {
                String minStr = split[0].substring(1);
                min = minStr.length() > 0 ? dateFormat.parse(minStr) : new Date(0);
                String maxStr = split[1].substring(0, split[1].length() - 1);
                max = maxStr.length() > 0 ? dateFormat.parse(maxStr) : new Date(92493356400000L);
            } catch (ParseException e) {
                min = new Date(0);
                max = new Date(92493356400000L);
            }
        }
        else {
            values = Arrays.stream(value.split(",")).map(v -> {
                try {
                    return dateFormat.parse(v);
                } catch (ParseException e) {
                    return new Date(0);
                }
            }).collect(Collectors.toList());
        }
    }

    @Override
    public String buildSql() {
        if (isRange) {
            return String.format("DATE(%s) BETWEEN ? and ?", field);
        }
        else {
            String[] params = new String[values.size()];
            Arrays.fill(params, "?");

            return String.format("DATE(%s) in (%s)", field, String.join(", ", params));
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
