package com.woowacourse.moamoa.studyroom.domain.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.woowacourse.moamoa.member.domain.Member;
import com.woowacourse.moamoa.studyroom.domain.Accessor;
import com.woowacourse.moamoa.studyroom.domain.StudyRoom;
import com.woowacourse.moamoa.studyroom.domain.exception.UneditableArticleException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class NoticeArticleTest {

    private static final long OWNER_ID = 1L;
    private static final long PARTICIPANT_ID = 2L;
    private static final long STUDY_ID = 1L;

    @DisplayName("공지 게시글은 방장만 수정할 수 있다.")
    @Test
    void update() {
        // arrange
        final Member owner = createMember(OWNER_ID);
        final StudyRoom studyRoom = createStudyRoom(owner);
        final NoticeArticle sut = createNoticeArticle(owner, studyRoom);

        final Accessor authorAccessor = new Accessor(owner.getId(), studyRoom.getId());

        // act
        sut.update(authorAccessor, new NoticeContent("수정된 제목", "수정된 내용"));

        // assert
        assertThat(sut.getContent()).isEqualTo(new NoticeContent("수정된 제목", "수정된 내용"));
    }

    @ParameterizedTest
    @DisplayName("스터디에 참여중인 방장 외에는 공지 게시글을 수정할 수 없다.")
    @MethodSource("provideForbiddenAccessor")
    void updateByNotAuthor(final Accessor forbiddenAccessor) {
        final Member owner = createMember(OWNER_ID);
        final Member participant = createMember(PARTICIPANT_ID);
        final StudyRoom studyRoom = createStudyRoom(owner, participant);
        final NoticeArticle sut = createNoticeArticle(owner, studyRoom);

        assertThatThrownBy(() -> sut.update(forbiddenAccessor, new NoticeContent("수정된 제목", "수정된 설명")))
                .isInstanceOf(UneditableArticleException.class);
    }

    @DisplayName("스터디에 참여한 방장만 공지 게시글을 삭제할 수 있다.")
    @Test
    void delete() {
        final Member owner = createMember(OWNER_ID);
        final StudyRoom studyRoom = createStudyRoom(owner);
        final NoticeArticle sut = createNoticeArticle(owner, studyRoom);

        sut.delete(new Accessor(OWNER_ID, STUDY_ID));

        assertThat(sut.isDeleted()).isTrue();
    }

    @ParameterizedTest
    @DisplayName("스터디에 참여한 방장 외에는 공지 게시글을 삭제할 수 없다.")
    @MethodSource("provideForbiddenAccessor")
    void deleteByNotAuthor(final Accessor forbiddenAccessor) {
        final Member owner = createMember(OWNER_ID);
        final Member participant = createMember(PARTICIPANT_ID);
        final StudyRoom studyRoom = createStudyRoom(owner, participant);
        final NoticeArticle sut = createNoticeArticle(owner, studyRoom);

        assertThatThrownBy(() -> sut.delete(forbiddenAccessor))
                .isInstanceOf(UneditableArticleException.class);
    }

    private Member createMember(final long id) {
        return new Member(id, id, "username" + id, "image", "profile");
    }

    private StudyRoom createStudyRoom(Member owner, Member... participant) {
        final Set<Long> participants = Stream.of(participant)
                .map(Member::getId)
                .collect(Collectors.toSet());
        return new StudyRoom(STUDY_ID, owner.getId(), participants);
    }

    private NoticeArticle createNoticeArticle(final Member owner, final StudyRoom studyRoom) {
        final Accessor accessor = new Accessor(owner.getId(), studyRoom.getId());
        final NoticeContent noticeContent = new NoticeContent("제목", "내용");
        return noticeContent.createArticle(studyRoom, accessor);
    }

    private static Stream<Arguments> provideForbiddenAccessor() {
        final long otherMemberId = OWNER_ID + 1;
        final long otherStudyId = STUDY_ID + 1;

        return Stream.of(
                Arguments.of(new Accessor(otherMemberId, STUDY_ID)),
                Arguments.of(new Accessor(OWNER_ID, otherStudyId)),
                Arguments.of(new Accessor(otherMemberId, otherStudyId))
        );
    }
}
