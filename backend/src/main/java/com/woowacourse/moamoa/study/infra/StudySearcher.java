package com.woowacourse.moamoa.study.infra;

import com.woowacourse.moamoa.filter.domain.CategoryName;
import com.woowacourse.moamoa.study.infra.response.StudiesResponse;
import com.woowacourse.moamoa.study.infra.response.StudyResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudySearcher {

    private static final RowMapper<StudyResponse> ROW_MAPPER = (rs, rn) -> {
        final Long id = rs.getLong("id");
        final String title = rs.getString("title");
        final String excerpt = rs.getString("excerpt");
        final String thumbnail = rs.getString("thumbnail");
        final String status = rs.getString("status");
        return new StudyResponse(id, title, excerpt, thumbnail, status);
    };

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StudiesResponse searchBy(final String title, final Filters filters, final Pageable pageable) {
        final List<StudyResponse> studies = namedParameterJdbcTemplate
                .query(sql(filters), params(title, filters, pageable), ROW_MAPPER);
        return new StudiesResponse(getCurrentPageStudies(studies, pageable), hasNext(studies, pageable));
    }

    private String sql(final Filters filters) {
        return "SELECT s.id, s.title, s.excerpt, s.thumbnail, s.status "
                + "FROM study s "
                + joinTableClause(filters)
                + "WHERE UPPER(s.title) LIKE UPPER(:title) ESCAPE '\' "
                + filtersInQueryClause(filters)
                + "GROUP BY s.id LIMIT :limit OFFSET :offset";
    }

    private String joinTableClause(final Filters filters) {
        String sql = "";
        if (filters.hasBy(CategoryName.GENERATION)) {
            sql += "JOIN study_filter sf1 ON s.id = sf1.study_id "
                    + "JOIN filter gen_filter ON sf1.filter_id = gen_filter.id "
                    + "JOIN category c1 ON gen_filter.category_id = c1.id AND c1.name = '" + CategoryName.GENERATION + "' ";
        }
        if (filters.hasBy(CategoryName.AREA)) {
            sql += "JOIN study_filter sf2 ON s.id = sf2.study_id "
                    + "JOIN filter area_filter ON sf2.filter_id = area_filter.id "
                    + "JOIN category c2 ON area_filter.category_id = c2.id AND c2.name = '" + CategoryName.AREA + "' ";
        }
        if (filters.hasBy(CategoryName.TAG)) {
            sql += "JOIN study_filter sf3 ON s.id = sf3.study_id "
                    + "JOIN filter tag_filter ON sf3.filter_id = tag_filter.id "
                    + "JOIN category c3 ON tag_filter.category_id = c3.id AND c3.name = '" + CategoryName.TAG + "' ";
        }
        return sql;
    }

    private String filtersInQueryClause(final Filters filters) {
        String sql = "";
        if (filters.hasBy(CategoryName.GENERATION)) {
            sql += "AND gen_filter.id IN (:generationIds) ";
        }
        if (filters.hasBy(CategoryName.AREA)) {
            sql += "AND area_filter.id IN (:areaIds) ";
        }
        if (filters.hasBy(CategoryName.TAG)) {
            sql += "AND tag_filter.id IN (:tagIds) ";
        }
        return sql;
    }

    private Map<String, Object> params(final String title, final Filters filters, final Pageable pageable) {
        Map<String, Object> param = new HashMap<>();
        param.put("title", "%" + title + "%");
        param.put("limit", pageable.getPageSize() + 1);
        param.put("offset", pageable.getOffset());
        param.put("generationIds", filters.getFilterIdsBy(CategoryName.GENERATION));
        param.put("areaIds", filters.getFilterIdsBy(CategoryName.AREA));
        param.put("tagIds", filters.getFilterIdsBy(CategoryName.TAG));
        return param;
    }

    private List<StudyResponse> getCurrentPageStudies(final List<StudyResponse> studies, final Pageable pageable) {
        if (hasNext(studies, pageable)) {
            return studies.subList(0, studies.size() - 1);
        }
        return studies;
    }

    private boolean hasNext(final List<StudyResponse> studies, final Pageable pageable) {
        return studies.size() > pageable.getPageSize();
    }
}
