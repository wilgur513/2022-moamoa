package com.woowacourse.moamoa.filter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.moamoa.filter.domain.CategoryId;
import com.woowacourse.moamoa.filter.query.FiltersSearcher;
import com.woowacourse.moamoa.filter.query.response.FiltersResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = @Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class SearchingFilterControllerTest {

    @Autowired
    private FiltersSearcher filtersSearcher;

    private SearchingFilterController searchingFilterController;

    @BeforeEach
    void setUp() {
        searchingFilterController = new SearchingFilterController(filtersSearcher);
    }

    @DisplayName("필터 목록 전체를 조회한다.")
    @Test
    void getFilters() {
        ResponseEntity<FiltersResponse> response = searchingFilterController.getFilters("", CategoryId.empty());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFilters())
                .extracting("id", "name", "category.id", "category.name")
                .containsExactly(
                        tuple(1L, "Java", 3L, "TAG"),
                        tuple(2L, "4기", 1L, "GENERATION"),
                        tuple(3L, "BE", 2L, "AREA"),
                        tuple(4L, "FE", 2L, "AREA"),
                        tuple(5L, "React", 3L, "TAG"),
                        tuple(6L, "3기", 1L, "GENERATION")
                );
    }

    @DisplayName("필터 이름을 대소문자 구분없이 앞뒤 공백을 제거해 필터 목록을 조회한다.")
    @Test
    void getFiltersByName() {
        ResponseEntity<FiltersResponse> response = searchingFilterController.getFilters("   ja  \t ", CategoryId.empty());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFilters())
                .extracting("id", "name", "category.id", "category.name")
                .containsExactly(
                        tuple(1L, "Java", 3L, "TAG")
                );
    }

    @Test
    void getFiltersByCategoryId() {
        ResponseEntity<FiltersResponse> response = searchingFilterController.getFilters("", new CategoryId(3L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFilters())
                .extracting("id", "name", "category.id", "category.name")
                .containsExactly(
                        tuple(1L, "Java", 3L, "TAG"),
                        tuple(5L, "React", 3L, "TAG")
                );
    }
}
