package com.woowacourse.moamoa.study.service;

import static com.woowacourse.moamoa.study.service.SearchFilter.emptyFilter;

import com.woowacourse.moamoa.study.service.response.StudiesResponse;
import com.woowacourse.moamoa.study.service.response.StudyResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StudyService {

    private final StudySearcher studySearcher;

    public StudiesResponse getStudies(final Pageable pageable) {
        final Slice<StudyResponse> slice = studySearcher.searchBy(emptyFilter(), pageable);
        return new StudiesResponse(slice.getContent(), slice.hasNext());
    }

    public StudiesResponse getStudies(final String title, final List<Long> tags, final Pageable pageable) {
        final Slice<StudyResponse> slice = studySearcher.searchBy(new SearchFilter(title.trim(), tags), pageable);
        return new StudiesResponse(slice.getContent(), slice.hasNext());
    }

}
