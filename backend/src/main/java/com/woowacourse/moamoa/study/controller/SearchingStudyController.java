package com.woowacourse.moamoa.study.controller;

import com.woowacourse.moamoa.study.infra.response.StudiesResponse;
import com.woowacourse.moamoa.study.infra.Filters;
import com.woowacourse.moamoa.study.infra.StudySearcher;
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

    private final StudySearcher studySearcher;

    public SearchingStudyController(final StudySearcher studySearcher) {
        this.studySearcher = studySearcher;
    }

    @GetMapping
    public ResponseEntity<StudiesResponse> searchStudies(
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        final StudiesResponse studiesResponse = studySearcher.searchBy(BLANK_TITLE, Filters.emptyFilters(), pageable);
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
        final Filters filters = new Filters(generations, areas, tags);
        final StudiesResponse studiesResponse = studySearcher.searchBy(title.trim(), filters, pageable);
        return ResponseEntity.ok().body(studiesResponse);
    }
}
