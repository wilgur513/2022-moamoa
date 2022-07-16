package com.woowacourse.moamoa.filter.controller;

import com.woowacourse.moamoa.filter.domain.CategoryId;
import com.woowacourse.moamoa.filter.query.FiltersSearcher;
import com.woowacourse.moamoa.filter.query.response.FiltersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchingFilterController {

    private final FiltersSearcher filtersSearcher;

    @GetMapping("/api/filters")
    public ResponseEntity<FiltersResponse> getFilters(
            @RequestParam(required = false, defaultValue = "") final String name,
            @RequestParam(value = "category", required = false, defaultValue = "") final CategoryId categoryId) {
        final FiltersResponse filtersResponse = filtersSearcher.queryBy(name.trim(), categoryId);
        return ResponseEntity.ok().body(filtersResponse);
    }
}
