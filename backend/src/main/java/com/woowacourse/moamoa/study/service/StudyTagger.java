package com.woowacourse.moamoa.study.service;

import com.woowacourse.moamoa.study.domain.AttachedTag;
import com.woowacourse.moamoa.study.domain.Study;
import com.woowacourse.moamoa.study.domain.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudyTagger {

    private final StudyRepository studyRepository;

    public StudyTagger(final StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public void attach(final Long studyId, final Long tagId) {
        final Study study = studyRepository.findById(studyId).orElseThrow();
        study.attachTag(new AttachedTag(tagId));
    }

    public void detach(final Long studyId, final Long tagId) {
        final Study study = studyRepository.findById(studyId).orElseThrow();
        study.detachTag(new AttachedTag(tagId));
    }
}
