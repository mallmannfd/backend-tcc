package com.company.demo.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import com.company.demo.util.dto.RangeDTO;

public class SpecUtil {

    private static final String TRANSLATE_FROM = "'áàâãäéèêëíìïóòôõöúùûüÁÀÂÃÄÉÈÊËÍÌÏÓÒÔÕÖÚÙÛÜçÇ'";
    private static final String TRANSLATE_TO = "'aaaaaeeeeiiiooooouuuuAAAAAEEEEIIIOOOOOUUUUcC'";

    private SpecUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Expression<String> removeAccents(CriteriaBuilder builder, Expression<String> expression) {
        return builder.function("translate", String.class, expression, builder.literal(TRANSLATE_FROM),
                builder.literal(TRANSLATE_TO));
    }

    public static Predicate toAndArray(CriteriaBuilder builder, Collection<Predicate> predicates) {
        predicates.removeIf(Objects::isNull);
        if (predicates.isEmpty()) {
            return null;
        }
        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    public static Predicate toOrArray(CriteriaBuilder builder, Collection<Predicate> predicates) {
        predicates.removeIf(Objects::isNull);
        if (predicates.isEmpty()) {
            return null;
        }
        return builder.or(predicates.toArray(new Predicate[predicates.size()]));
    }

    public static Expression<String> concatAll(CriteriaBuilder builder, Expression<?>... x) {
        var result = x[0].as(String.class);
        for (var i = 1; i < x.length; i++) {
            result = builder.concat(result, x[i].as(String.class));
        }
        return result;
    }

    public static <F> Predicate isEqual(CriteriaBuilder builder, Expression<?> path, F filter) {
        return filter == null ? null : builder.equal(path, filter);
    }

    public static Predicate like(CriteriaBuilder builder, Expression<?> path, String filter) {
        if (filter == null || filter.isBlank()) {
            return null;
        }
        var filterValue = filter.replace("*", "%").trim().toUpperCase();
        return builder.like(builder.upper(builder.trim(path.as(String.class))), filterValue);
    }

    public static Predicate contains(CriteriaBuilder builder, Expression<?> path, String filter) {
        return filter == null ? null : like(builder, path, "%" + filter.trim() + "%");
    }

    public static Predicate startsWith(CriteriaBuilder builder, Expression<?> path, String filter) {
        return filter == null ? null : like(builder, path, filter.trim() + "%");
    }

    public static Predicate isIn(Path<Object> path, Collection<?> values) {
        return values == null || values.isEmpty() ? null : path.in(values);
    }

    public static Predicate isBetween(CriteriaBuilder builder, Expression<LocalDateTime> path, RangeDTO range) {
        if (range == null) {
            return null;
        }
        var predicates = new ArrayList<Predicate>();
        if (range.getStart() != null) {
            predicates.add(builder.greaterThanOrEqualTo(path, range.getStart().atStartOfDay()));
        }
        if (range.getEnd() != null) {
            predicates.add(builder.lessThanOrEqualTo(path, range.getEnd().atTime(LocalTime.MAX)));
        }
        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    public static Predicate isDeleted(CriteriaBuilder builder, Path<?> path, Boolean deleted) {
        return deleted == null ? builder.and() : isEqual(builder, path.get("deleted"), deleted);
    }

    public static Predicate byFilter(CriteriaBuilder builder, Expression<?> root, String filter, String... columns) {
        if (filter == null || filter.trim().isEmpty()) {
            return builder.and();
        }
        var predicates = new ArrayList<Predicate>();
        for (var col : Arrays.asList(columns)) {
            var path = (Path<?>) root;
            if (col.contains(".")) {
                var split = col.split("[.]");
                for (var i = 0; i < split.length - 1; i++) {
                    path = ((From<?, ?>) path).join(split[i], JoinType.LEFT);
                }
                path = path.<String>get(split[split.length - 1]);
            } else {
                path = path.<String>get(col);
            }
            predicates.add(contains(builder, path, filter));
        }
        return toOrArray(builder, predicates);
    }
}
