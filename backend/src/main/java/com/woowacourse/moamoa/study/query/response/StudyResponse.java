package com.woowacourse.moamoa.study.query.response;

import lombok.ToString;

@ToString
public class StudyResponse {

    private final Long id;
    private final String title;
    private final String excerpt;
    private final String thumbnail;
    private final String status;

    public StudyResponse(
            final Long id, final String title, final String excerpt,
            final String thumbnail, final String status
    ) {
        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.thumbnail = thumbnail;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getStatus() {
        return status;
    }
}
