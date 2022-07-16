package com.woowacourse.moamoa.study.infra;

import com.woowacourse.moamoa.filter.domain.CategoryName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Filters {

    private final Map<CategoryName, List<Long>> filters = new HashMap<>();

    public Filters(final List<Long> generationIds, final List<Long> areaIds, final List<Long> tagIds) {
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

    public static Filters emptyFilters() {
        return new Filters(List.of(), List.of(), List.of());
    }
}
