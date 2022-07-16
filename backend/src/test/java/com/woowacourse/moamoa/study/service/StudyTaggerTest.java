package com.woowacourse.moamoa.study.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.moamoa.study.domain.Study;
import com.woowacourse.moamoa.study.domain.repository.StudyRepository;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class StudyTaggerTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void attachAndDetachTag() {
        StudyTagger studyTagger = new StudyTagger(studyRepository);

        studyTagger.attach(5L, 3L);
        entityManager.flush();

        studyTagger.attach(5L, 4L);
        entityManager.flush();

        studyTagger.attach(5L, 5L);
        entityManager.flush();

        studyTagger.detach(5L, 4L);
        entityManager.flush();

        studyTagger.detach(5L, 5L);
        entityManager.flush();

        entityManager.clear();

        Study study = studyRepository.findById(5L).orElseThrow();

        System.out.println(study.getAttachedTags());

        assertThat(study.getAttachedTags())
                .extracting("tagId")
                .containsExactlyInAnyOrder(2L, 3L);
    }
}
