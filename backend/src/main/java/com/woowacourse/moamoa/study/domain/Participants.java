package com.woowacourse.moamoa.study.domain;

import static com.woowacourse.moamoa.study.domain.RecruitmentStatus.RECRUITMENT_END;
import static com.woowacourse.moamoa.study.domain.RecruitmentStatus.RECRUITMENT_START;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@ToString
@NoArgsConstructor(access = PROTECTED)
public class Participants {

    @Column(name = "current_member_count")
    private int size;

    @Column(name = "max_member_count")
    private Integer max;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private RecruitmentStatus recruitmentStatus;

    @ElementCollection
    @CollectionTable(name = "study_member", joinColumns = @JoinColumn(name = "study_id"))
    private Set<Participant> participants = new HashSet<>();

    public Participants(final Integer size, final Integer max,
                        final Set<Participant> participants, Long ownerId, RecruitmentStatus recruitmentStatus) {
        this.size = size;
        this.max = max;
        this.participants = participants;
        this.ownerId = ownerId;
        this.recruitmentStatus = recruitmentStatus;
    }

    public static Participants createByMaxSizeAndOwnerId(final Integer maxSize, Long ownerId) {
        return new Participants(1, maxSize, new HashSet<>(), ownerId, RECRUITMENT_START);
    }

    public List<Participant> getParticipants() {
        return new ArrayList<>(participants);
    }

    public int getCurrentMemberSize() {
        return size;
    }

    public RecruitmentStatus getRecruitmentStatus() {
        return recruitmentStatus;
    }

    void participate(final Participant participant) {
        participants.add(participant);
        size = size + 1;
        closeRecruitmentWhenMaxMember();
    }

    private void closeRecruitmentWhenMaxMember() {
        if (size == max) {
            this.recruitmentStatus = RECRUITMENT_END;
        }
    }

    boolean isImpossibleParticipation(Long memberId) {
        return isInvalidMemberSize() || isAlreadyParticipation(memberId) || isCloseStatus();
    }

    private boolean isInvalidMemberSize() {
        return max != null && max <= size;
    }

    private boolean isAlreadyParticipation(final Long memberId) {
        final Participant participant = new Participant(memberId);
        return isOwner(memberId) || isParticipated(participant);
    }

    private boolean isCloseStatus() {
        return recruitmentStatus.equals(RECRUITMENT_END);
    }

    private boolean isOwner(final Long memberId) {
        return Objects.equals(memberId, ownerId);
    }

    private boolean isParticipated(final Participant participant) {
        return participants.contains(participant);
    }

    public boolean contains(final Participant participant) {
        return participants.contains(participant) || ownerId.equals(participant.getMemberId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Participants that = (Participants) o;
        return size == that.size && Objects.equals(max, that.max) && Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(getParticipants(), that.getParticipants());
    }

    @Override
    public int hashCode()  {
        return Objects.hash(size, max, ownerId, participants);
    }
}
