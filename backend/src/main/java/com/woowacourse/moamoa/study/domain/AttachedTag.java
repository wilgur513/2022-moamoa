package com.woowacourse.moamoa.study.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.ToString;

@Embeddable
@ToString
public class AttachedTag {

    @Column(name = "filter_id", nullable = false)
    private Long tagId;

    protected AttachedTag() { }

    public AttachedTag(final Long tagId) {
        this.tagId = tagId;
    }

    public Long getTagId() {
        return tagId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AttachedTag that = (AttachedTag) o;
        return Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }
}
