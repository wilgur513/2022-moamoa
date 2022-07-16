package com.woowacourse.moamoa.study.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class Study {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String title;
    private String excerpt;
    private String thumbnail;
    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "study_filter",
            joinColumns = @JoinColumn(name = "study_id")
    )
    private final Set<AttachedTag> attachedTags = new HashSet<>();

    protected Study() {
    }

    public Study(final Long id, final String title, final String excerpt,
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

    public void attachTag(final AttachedTag attachedTag) {
        attachedTags.add(attachedTag);
    }

    public void detachTag(final AttachedTag attachedTag) {
        attachedTags.remove(attachedTag);
    }

    public Set<AttachedTag> getAttachedTags() {
        return attachedTags;
    }
}
