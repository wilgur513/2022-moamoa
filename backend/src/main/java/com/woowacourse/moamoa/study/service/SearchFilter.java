package com.woowacourse.moamoa.study.service;

import java.util.List;

public class SearchFilter {

    private final String title;
    private final List<Long> filterIds;

    public SearchFilter(final String title, final List<Long> filterIds) {
        this.title = title;
        this.filterIds = filterIds;
    }

    public String getTitle() {
        return title;
    }

    public List<Long> getFilterIds() {
        return filterIds;
    }

    public boolean hasFilters() {
        return !filterIds.isEmpty();
    }

    public int getFilterSize() {
        return filterIds.size();
    }

    public static SearchFilter emptyFilter() {
        return new SearchFilter("", List.of());
    }
}
