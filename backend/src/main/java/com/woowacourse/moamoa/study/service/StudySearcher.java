package com.woowacourse.moamoa.study.service;

import com.woowacourse.moamoa.study.service.response.StudyResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StudySearcher {

    Slice<StudyResponse> searchBy(SearchFilter filter, Pageable pageable);
}
