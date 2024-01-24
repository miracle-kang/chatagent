package com.miraclekang.chatgpt.common.repo;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SearchOperation {
    EQ {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            var expression = expression(root, key);
            return builder.equal(expression, castToRequiredType(expression.getJavaType(), value));
        }
    }, NE {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            var expression = expression(root, key);
            return builder.notEqual(expression, castToRequiredType(expression.getJavaType(), value));
        }
    }, GT {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.greaterThan(expression(root, key), value);
        }
    }, LT {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.lessThan(expression(root, key), value);
        }
    }, GOE {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.greaterThanOrEqualTo(expression(root, key), value);
        }
    }, LOE {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.lessThanOrEqualTo(expression(root, key), value);
        }
    }, IN {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            Expression<Object> expression = expression(root, key);
            return builder.in(expression)
                    .value(castToRequiredType(expression.getJavaType(), Arrays.asList(value.split("\\|"))));
        }
    }, LIKE {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.like(expression(root, key), value);
        }
    }, STARTS_WITH {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.like(expression(root, key), value + "%");
        }
    }, ENDS_WITH {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.like(expression(root, key), "%" + value);
        }
    }, CONTAINS {
        @Override
        public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
            return builder.like(expression(root, key), "%" + value + "%");
        }
    };

    public Predicate toPredicate(CriteriaBuilder builder, Root<?> root, String key, String value) {
        return null;
    }

    private static Object castToRequiredType(Class<?> fieldType, String value) {
        if (fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return Enum.valueOf((Class) fieldType, value);
        }
        return value;
    }

    private static Object castToRequiredType(Class<?> fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }

    private static <T> Expression<T> expression(Root<?> root, String key) {
        String[] joinKey = key.split("\\.");
        if (joinKey.length == 1) {
            return root.get(key);
        }

        var join = root.join(joinKey[0]);
        for (int i = 1; i < joinKey.length - 1; i++) {
            join = join.join(joinKey[i]);
        }
        return join.get(joinKey[joinKey.length - 1]);
    }

    public static SearchOperation of(String input) {
        return switch (input) {
            case ":" -> EQ;
            case "!" -> NE;
            case ">" -> GT;
            case ">:" -> GOE;
            case "<" -> LT;
            case "<:" -> LOE;
            case "::" -> IN;
            case "~" -> LIKE;
            case ":~" -> STARTS_WITH;
            case "~:" -> ENDS_WITH;
            case "~:~" -> CONTAINS;
            default -> null;
        };
    }
}