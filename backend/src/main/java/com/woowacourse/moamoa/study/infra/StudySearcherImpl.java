package com.woowacourse.moamoa.study.infra;

import com.woowacourse.moamoa.common.infra.SliceMapper;
import com.woowacourse.moamoa.study.service.SearchFilter;
import com.woowacourse.moamoa.study.service.StudySearcher;
import com.woowacourse.moamoa.study.service.response.StudyResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudySearcherImpl implements StudySearcher {

    private static final SliceMapper SLICE_MAPPER = new SliceMapper();
    private static final RowMapper<StudyResponse> ROW_MAPPER = (rs, rn) -> {
        final Long id = rs.getLong("id");
        final String title = rs.getString("title");
        final String excerpt = rs.getString("excerpt");
        final String thumbnail = rs.getString("thumbnail");
        final String status = rs.getString("status");
        return new StudyResponse(id, title, excerpt, thumbnail, status);
    };

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Slice<StudyResponse> searchBy(final SearchFilter filter,
                                         final Pageable pageable) {
        List<StudyResponse> studies = namedParameterJdbcTemplate
                .query(sql(filter), params(filter, pageable), ROW_MAPPER);
        return SLICE_MAPPER.mapToSlice(studies, pageable);
    }

    private String sql(final SearchFilter filter) {
        if (filter.hasFilters()) {
            return "SELECT s.id, s.title, s.excerpt, s.thumbnail, s.status "
                    + "FROM study AS s JOIN study_filter AS f ON s.id = f.study_id "
                    + "WHERE UPPER(s.title) LIKE UPPER(:title) ESCAPE '\' AND f.filter_id IN (:filterIds)"
                    + "GROUP BY s.id "
                    + "HAVING count(s.id) = :filterSize "
                    + "LIMIT :limit OFFSET :offset";
        }

        return "SELECT s.id, s.title, s.excerpt, s.thumbnail, s.status "
                + "FROM study AS s JOIN study_filter AS f ON s.id = f.study_id "
                + "WHERE UPPER(s.title) LIKE UPPER(:title) ESCAPE '\' "
                + "GROUP BY s.id "
                + "LIMIT :limit OFFSET :offset";
    }

    private Map<String, Object> params(final SearchFilter filter, final Pageable pageable) {
        Map<String, Object> param = new HashMap<>();
        param.put("title", "%" + filter.getTitle() + "%");
        param.put("limit", pageable.getPageSize() + 1);
        param.put("offset", pageable.getOffset());

        if (filter.hasFilters()) {
            param.put("filterIds", filter.getFilterIds());
            param.put("filterSize", filter.getFilterSize());
        }

        return param;
    }
}
