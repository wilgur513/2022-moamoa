package com.woowacourse.moamoa.studyroom.domain.repository.article;

import com.woowacourse.moamoa.studyroom.domain.article.NoticeArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeArticleRepository extends JpaRepository<NoticeArticle, Long> {
}
