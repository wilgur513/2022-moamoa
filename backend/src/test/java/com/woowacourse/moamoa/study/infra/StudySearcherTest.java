package com.woowacourse.moamoa.study.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.moamoa.study.service.SearchFilter;
import com.woowacourse.moamoa.study.service.response.StudyResponse;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class StudySearcherTest {

    @Autowired
    private StudySearcherImpl studySearcher;

    @DisplayName("페이징 정보를 사용해 스터디 목록 조회")
    @ParameterizedTest
    @MethodSource("providePageableAndExpect")
    public void findAllByPageable(Pageable pageable, List<Tuple> expectedTuples,
                                  boolean expectedHasNext) {
        final Slice<StudyResponse> slice = studySearcher.searchBy(new SearchFilter("", List.of()), pageable);

        assertThat(slice.hasNext()).isEqualTo(expectedHasNext);
        assertThat(slice.getContent())
                .hasSize(expectedTuples.size())
                .filteredOn(study -> study.getId() != null)
                .extracting("title", "excerpt", "thumbnail", "status")
                .containsExactlyElementsOf(expectedTuples);
    }

    private static Stream<Arguments> providePageableAndExpect() {
        List<Tuple> tuples = List.of(
                tuple("Java 스터디", "자바 설명", "java thumbnail", "OPEN"),
                tuple("React 스터디", "리액트 설명", "react thumbnail", "OPEN"),
                tuple("javaScript 스터디", "자바스크립트 설명", "javascript thumbnail", "OPEN"),
                tuple("HTTP 스터디", "HTTP 설명", "http thumbnail", "CLOSE"),
                tuple("알고리즘 스터디", "알고리즘 설명", "algorithm thumbnail", "CLOSE"));

        return Stream.of(
                Arguments.of(PageRequest.of(0, 3), tuples.subList(0, 3), true),
                Arguments.of(PageRequest.of(1, 2), tuples.subList(2, 4), true),
                Arguments.of(PageRequest.of(1, 3), tuples.subList(3, 5), false)
        );
    }

    @DisplayName("키워드와 함께 페이징 정보를 사용해 스터디 목록 조회")
    @Test
    public void findByTitleContaining() {
        final Slice<StudyResponse> slice = studySearcher.searchBy(new SearchFilter("java", List.of()), PageRequest.of(0, 3));

        assertThat(slice.hasNext()).isFalse();
        assertThat(slice.getContent())
                .hasSize(2)
                .filteredOn(study -> study.getId() != null)
                .extracting("title", "excerpt", "thumbnail", "status")
                .containsExactly(
                        tuple("Java 스터디", "자바 설명", "java thumbnail", "OPEN"),
                        tuple("javaScript 스터디", "자바스크립트 설명", "javascript thumbnail", "OPEN"));
    }

    @DisplayName("빈 키워드와 함께 페이징 정보를 사용해 스터디 목록 조회")
    @Test
    public void findByBlankTitle() {
        final Slice<StudyResponse> slice = studySearcher.searchBy(new SearchFilter("", List.of()), PageRequest.of(0, 5));

        assertThat(slice.hasNext()).isFalse();
        assertThat(slice.getContent())
                .hasSize(5)
                .filteredOn(study -> study.getId() != null)
                .extracting("title", "excerpt", "thumbnail", "status")
                .containsExactly(
                        tuple("Java 스터디", "자바 설명", "java thumbnail", "OPEN"),
                        tuple("React 스터디", "리액트 설명", "react thumbnail", "OPEN"),
                        tuple("javaScript 스터디", "자바스크립트 설명", "javascript thumbnail", "OPEN"),
                        tuple("HTTP 스터디", "HTTP 설명", "http thumbnail", "CLOSE"),
                        tuple("알고리즘 스터디", "알고리즘 설명", "algorithm thumbnail", "CLOSE"));
    }

    @DisplayName("찾을 수 없는 태그로 스터디 목록을 조회")
    @Test
    void searchByUnableToFoundTags() {
        Slice<StudyResponse> studies = studySearcher.searchBy(new SearchFilter("", List.of(3L, 4L)), PageRequest.of(0, 3));

        assertThat(studies.hasNext()).isFalse();
        assertThat(studies.getContent()).hasSize(0);
    }

    @DisplayName("태그로 스터디 목록을 조회")
    @Test
    void searchByTags() {
        Slice<StudyResponse> studies = studySearcher.searchBy(new SearchFilter("", List.of(1L, 2L, 3L)), PageRequest.of(0, 3));

        assertThat(studies.hasNext()).isFalse();
        assertThat(studies.getContent())
                .hasSize(1)
                .extracting("title", "excerpt", "thumbnail", "status")
                .containsExactlyInAnyOrder(
                        tuple("Java 스터디", "자바 설명", "java thumbnail", "OPEN")
                );
    }

    @DisplayName("태그로 중간 페이지의 스터디 목록을 조회")
    @Test
    void searchHasNextPageByTags() {
        Slice<StudyResponse> studies = studySearcher.searchBy(new SearchFilter("", List.of(2L)), PageRequest.of(0, 3));

        assertThat(studies.hasNext()).isTrue();
        assertThat(studies.getContent())
                .hasSize(3)
                .extracting("title", "excerpt", "thumbnail", "status")
                .containsExactlyInAnyOrder(
                        tuple("Java 스터디", "자바 설명", "java thumbnail", "OPEN"),
                        tuple("React 스터디", "리액트 설명", "react thumbnail", "OPEN"),
                        tuple("javaScript 스터디", "자바스크립트 설명", "javascript thumbnail", "OPEN")
                );
    }
}
