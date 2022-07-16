package com.woowacourse.moamoa.study.controller;

import com.woowacourse.moamoa.study.query.SearchingFilters;
import com.woowacourse.moamoa.study.query.StudiesSearcher;
import com.woowacourse.moamoa.study.query.response.StudiesResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/studies")
public class SearchingStudyController {

    private static final String BLANK_TITLE = "";

    private final StudiesSearcher studiesSearcher;

    public SearchingStudyController(final StudiesSearcher studiesSearcher) {
        this.studiesSearcher = studiesSearcher;
    }

    @GetMapping
    public ResponseEntity<StudiesResponse> searchStudies(
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        final StudiesResponse studiesResponse = studiesSearcher
                .searchBy(BLANK_TITLE, SearchingFilters.emptyFilters(), pageable);
        return ResponseEntity.ok().body(studiesResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<StudiesResponse> searchStudies(
            @RequestParam(required = false, defaultValue = "") final String title,
            @RequestParam(required = false, name = "generation", defaultValue = "") final List<Long> generations,
            @RequestParam(required = false, name = "area", defaultValue = "") final List<Long> areas,
            @RequestParam(required = false, name = "tag", defaultValue = "") final List<Long> tags,
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        final SearchingFilters searchingFilters = new SearchingFilters(generations, areas, tags);
        final StudiesResponse studiesResponse = studiesSearcher.searchBy(title.trim(), searchingFilters, pageable);
        return ResponseEntity.ok().body(studiesResponse);
    }
}
