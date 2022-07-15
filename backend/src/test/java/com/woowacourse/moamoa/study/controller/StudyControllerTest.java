package com.woowacourse.moamoa.study.controller;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.moamoa.study.service.StudySearcher;
import com.woowacourse.moamoa.study.service.StudyService;
import com.woowacourse.moamoa.study.service.response.StudiesResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class StudyControllerTest {

    private StudyController studyController;

    @Autowired
    private StudySearcher studySearcher;

    @BeforeEach
    void setUp() {
        studyController = new StudyController(new StudyService(studySearcher));
    }

    @DisplayName("페이징 정보로 스터디 목록 조회")
    @Test
    public void getStudies() {
        ResponseEntity<StudiesResponse> response = studyController.getStudies(PageRequest.of(0, 3));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isHasNext()).isTrue();
        assertThat(response.getBody().getStudies())
                .hasSize(3)
                .extracting("id", "title", "excerpt", "thumbnail", "status")
                .containsExactlyElementsOf(List.of(
                        tuple(1L, "Java 스터디", "자바 설명", "java thumbnail", "OPEN"),
                        tuple(2L, "React 스터디", "리액트 설명", "react thumbnail", "OPEN"),
                        tuple(3L, "javaScript 스터디", "자바스크립트 설명", "javascript thumbnail", "OPEN"))
                );
    }

    @DisplayName("빈 문자열로 검색시 전체 스터디 목록에서 조회")
    @Test
    void searchByBlankKeyword() {
        ResponseEntity<StudiesResponse> response = studyController.searchStudies("", emptyList(),
                PageRequest.of(0, 3)
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isHasNext()).isTrue();
        assertThat(response.getBody().getStudies())
                .hasSize(3)
                .extracting("id", "title", "excerpt", "thumbnail", "status")
                .containsExactlyElementsOf(List.of(
                        tuple(1L, "Java 스터디", "자바 설명", "java thumbnail", "OPEN"),
                        tuple(2L, "React 스터디", "리액트 설명", "react thumbnail", "OPEN"),
                        tuple(3L, "javaScript 스터디", "자바스크립트 설명", "javascript thumbnail", "OPEN"))
                );
    }

    @DisplayName("문자열로 검색시 해당되는 스터디 목록에서 조회")
    @Test
    void searchByKeyword() {
        ResponseEntity<StudiesResponse> response = studyController.searchStudies("Java 스터디", emptyList(),
                PageRequest.of(0, 3)
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isHasNext()).isFalse();
        assertThat(response.getBody().getStudies())
                .hasSize(1)
                .extracting("id", "title", "excerpt", "thumbnail", "status")
                .contains(tuple(1L, "Java 스터디", "자바 설명", "java thumbnail", "OPEN"));
    }

    @DisplayName("앞뒤 공백을 제거한 문자열로 스터디 목록 조회")
    @Test
    void searchWithTrimKeyword() {
        ResponseEntity<StudiesResponse> response = studyController
                .searchStudies("   Java 스터디   ", emptyList(), PageRequest.of(0, 3));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isHasNext()).isFalse();
        assertThat(response.getBody().getStudies())
                .hasSize(1)
                .extracting("id", "title", "excerpt", "thumbnail", "status")
                .contains(tuple(1L, "Java 스터디", "자바 설명", "java thumbnail", "OPEN"));
    }

    @DisplayName("tag에 필터링 되는 스터디 목록이 없는 경우 빈 리스트를 반환한다.")
    @Test
    void searchNotFoundStudiesByFilters() {
        List<Long> filterIds = List.of(3L, 4L); // BE, FE
        ResponseEntity<StudiesResponse> response = studyController
                .searchStudies("", filterIds, PageRequest.of(0, 3));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isHasNext()).isFalse();
        assertThat(response.getBody().getStudies())
                .hasSize(0);
    }

    @DisplayName("tag에 필터링 되는 스터디 목록을 조회한다.")
    @Test
    void searchStudiesByFilters() {
        List<Long> filterIds = List.of(1L, 2L); // Java, 4기
        ResponseEntity<StudiesResponse> response = studyController
                .searchStudies("", filterIds, PageRequest.of(0, 3));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isHasNext()).isFalse();
        assertThat(response.getBody().getStudies())
                .hasSize(1)
                .extracting("id", "title", "excerpt", "thumbnail", "status")
                .contains(tuple(1L, "Java 스터디", "자바 설명", "java thumbnail", "OPEN"));
    }
}
