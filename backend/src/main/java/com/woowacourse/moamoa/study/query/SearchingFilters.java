package com.woowacourse.moamoa.study.query;

import com.woowacourse.moamoa.filter.domain.CategoryName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class SearchingFilters {

    private final Map<CategoryName, List<Long>> filters = new HashMap<>();

    public SearchingFilters(final List<Long> generationIds, final List<Long> areaIds, final List<Long> tagIds) {
        filters.put(CategoryName.GENERATION, generationIds);
        filters.put(CategoryName.AREA, areaIds);
        filters.put(CategoryName.TAG, tagIds);
    }

    public boolean hasBy(CategoryName name) {
        return !filters.get(name).isEmpty();
    }

    public List<Long> getFilterIdsBy(CategoryName name) {
        return filters.get(name);
    }

    public static SearchingFilters emptyFilters() {
        return new SearchingFilters(List.of(), List.of(), List.of());
    }
}
