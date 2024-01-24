package com.miraclekang.chatgpt.common.restapi;

import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchKeyMapping;
import com.miraclekang.chatgpt.common.repo.SearchOperation;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ParameterObject
public class SearchCriteriaParam {

    private static final Pattern SEARCH_PATTERN = Pattern.compile("([\\w._]+?)([:<>~!]+)([\\w|.-_@]+?),",
            Pattern.UNICODE_CHARACTER_CLASS);

    private String search;

    public SearchCriteriaParam() {
    }

    public SearchCriteriaParam(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<SearchCriteria> toCriteria() {
        return toCriteria(SearchKeyMapping.defaultMapping());
    }

    public List<SearchCriteria> toCriteria(SearchKeyMapping mapping) {
        if (StringUtils.isBlank(search)) {
            return List.of();
        }
        Matcher matcher = SEARCH_PATTERN.matcher(search + ",");
        List<SearchCriteria> criteriaList = new ArrayList<>();
        while (matcher.find()) {
            criteriaList.add(new SearchCriteria(
                    mapping.apply(matcher.group(1)),
                    SearchOperation.of(matcher.group(2)),
                    matcher.group(3)
            ));
        }

        return criteriaList;
    }
}
