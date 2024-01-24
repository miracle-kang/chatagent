package com.miraclekang.chatgpt.common.repo;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SearchSpecification<T> implements Specification<T> {

    private final SearchCriteria criteria;

    public SearchSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {
        return criteria.toPredicate(root, criteriaBuilder);
    }

    public static <T> Specification<T> of(SearchCriteria criteria) {
        return new SearchSpecification<>(criteria);
    }

    @SuppressWarnings("unchecked")
    public static <T> Specification<T> allOf(List<SearchCriteria> criteriaIterable) {
        return Specification.allOf(criteriaIterable.stream()
                .map(criteria -> (Specification<T>) new SearchSpecification<>(criteria))
                .toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> Specification<T> anyOf(List<SearchCriteria> criteriaIterable) {
        return Specification.anyOf(criteriaIterable.stream()
                .map(criteria -> (Specification<T>) new SearchSpecification<>(criteria))
                .toList());
    }
}
