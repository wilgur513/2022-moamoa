package com.woowacourse.moamoa.filter.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.moamoa.filter.domain.CategoryId;
import com.woowacourse.moamoa.filter.query.response.FiltersResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = @Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class FiltersSearcherTest {

    @Autowired
    private FiltersSearcher filtersSearcher;

    @DisplayName("필터 없이 조회시 태그 목록 전체를 조회한다.")
    @Test
    void findAllByBlankTagName() {
        FiltersResponse filterResponses = filtersSearcher.queryBy("", CategoryId.empty());

        assertThat(filterResponses.getFilters())
                .hasSize(6)
                .filteredOn(filter -> filter.getId() != null)
                .extracting("name", "category.id", "category.name")
                .containsExactly(
                        tuple("Java", 3L, "TAG"),
                        tuple("4기", 1L, "GENERATION"),
                        tuple("BE", 2L, "AREA"),
                        tuple("FE", 2L, "AREA"),
                        tuple("React", 3L, "TAG"),
                        tuple("3기", 1L, "GENERATION")
                );
    }

    @DisplayName("대소문자 구분없이 필터 이름으로 조회한다.")
    @Test
    void findAllByNameContainingIgnoreCase() {
        FiltersResponse filterResponses = filtersSearcher.queryBy("ja", CategoryId.empty());

        assertThat(filterResponses.getFilters())
                .hasSize(1)
                .filteredOn(filter -> filter.getId() != null)
                .extracting("name", "category.id", "category.name")
                .containsExactly(
                        tuple("Java", 3L, "TAG")
                );
    }

    @DisplayName("카테고리로 필터를 조회한다.")
    @Test
    void findAllByCategory() {
        FiltersResponse filterResponses = filtersSearcher.queryBy("", new CategoryId(3L));

        assertThat(filterResponses.getFilters())
                .hasSize(2)
                .filteredOn(filter -> filter.getId() != null)
                .extracting("name", "category.id", "category.name")
                .containsExactly(
                        tuple("Java", 3L, "TAG"),
                        tuple("React", 3L, "TAG")
                );
    }

    @DisplayName("카테고리와 이름으로 필터를 조회한다.")
    @Test
    void findAllByCategoryAndName() {
        FiltersResponse filterResponses = filtersSearcher.queryBy("ja", new CategoryId(3L));

        assertThat(filterResponses.getFilters())
                .hasSize(1)
                .filteredOn(filter -> filter.getId() != null)
                .extracting("name", "category.id", "category.name")
                .containsExactly(
                        tuple("Java", 3L, "TAG")
                );
    }
}
