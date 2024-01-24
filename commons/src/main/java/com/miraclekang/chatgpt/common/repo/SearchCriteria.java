package com.miraclekang.chatgpt.common.repo;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.util.Objects;

import static com.miraclekang.chatgpt.common.repo.SearchOperation.*;

public class SearchCriteria implements Serializable {

    private String key;
    private SearchOperation operation;
    private Object value;

    public SearchCriteria(String key, SearchOperation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SearchOperation getOperation() {
        return operation;
    }

    public void setOperation(SearchOperation operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Predicate toPredicate(Root<?> root, CriteriaBuilder builder) {
        if (this.key == null || this.operation == null) {
            return null;
        }
        String value = this.value.getClass().isEnum() ? ((Enum<?>) this.value).name() : this.value.toString();
        return operation.toPredicate(builder, root, key, value);
    }

    public static SearchCriteria eq(String key, Object value) {
        return new SearchCriteria(key, EQ, value);
    }

    public static SearchCriteria ne(String key, Object value) {
        return new SearchCriteria(key, NE, value);
    }

    public static SearchCriteria gt(String key, Object value) {
        return new SearchCriteria(key, GT, value);
    }

    public static SearchCriteria goe(String key, Object value) {
        return new SearchCriteria(key, GOE, value);
    }

    public static SearchCriteria lt(String key, Object value) {
        return new SearchCriteria(key, LT, value);
    }

    public static SearchCriteria loe(String key, Object value) {
        return new SearchCriteria(key, LOE, value);
    }

    public static SearchCriteria like(String key, Object value) {
        return new SearchCriteria(key, LIKE, value);
    }

    public static SearchCriteria startsWith(String key, Object value) {
        return new SearchCriteria(key, STARTS_WITH, value);
    }

    public static SearchCriteria endsWith(String key, Object value) {
        return new SearchCriteria(key, ENDS_WITH, value);
    }

    public static SearchCriteria contains(String key, Object value) {
        return new SearchCriteria(key, CONTAINS, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchCriteria that = (SearchCriteria) o;
        return Objects.equals(key, that.key) && operation == that.operation && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, operation, value);
    }

    @Override
    public String toString() {
        return "SearchCriteria{" +
                "key='" + key + '\'' +
                ", operation=" + operation +
                ", value=" + value +
                '}';
    }
}
