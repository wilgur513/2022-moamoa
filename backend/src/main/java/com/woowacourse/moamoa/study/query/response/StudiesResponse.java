package com.woowacourse.moamoa.study.query.response;

import java.util.List;

public class StudiesResponse {

    private final List<StudyResponse> studies;
    private final boolean hasNext;

    public StudiesResponse(final List<StudyResponse> studies, final boolean hasNext) {
        this.studies = studies;
        this.hasNext = hasNext;
    }

    public List<StudyResponse> getStudies() {
        return studies;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
