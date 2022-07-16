package com.woowacourse.moamoa.study.query;

import com.woowacourse.moamoa.filter.domain.CategoryName;
import com.woowacourse.moamoa.study.query.response.StudiesResponse;
import com.woowacourse.moamoa.study.query.response.StudyResponse;
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
public class StudiesSearcher {

    private static final RowMapper<StudyResponse> ROW_MAPPER = (rs, rn) -> {
        final Long id = rs.getLong("id");
        final String title = rs.getString("title");
        final String excerpt = rs.getString("excerpt");
        final String thumbnail = rs.getString("thumbnail");
        final String status = rs.getString("status");
        return new StudyResponse(id, title, excerpt, thumbnail, status);
    };

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StudiesResponse searchBy(final String title, final SearchingFilters searchingFilters,
                                    final Pageable pageable) {
        final List<StudyResponse> studies = namedParameterJdbcTemplate
                .query(sql(searchingFilters), params(title, searchingFilters, pageable), ROW_MAPPER);
        return new StudiesResponse(getCurrentPageStudies(studies, pageable), hasNext(studies, pageable));
    }

    private String sql(final SearchingFilters searchingFilters) {
        return "SELECT s.id, s.title, s.excerpt, s.thumbnail, s.status "
                + "FROM study s "
                + joinTableClause(searchingFilters)
                + "WHERE UPPER(s.title) LIKE UPPER(:title) ESCAPE '\' "
                + filtersInQueryClause(searchingFilters)
                + "GROUP BY s.id LIMIT :limit OFFSET :offset";
    }

    private String joinTableClause(final SearchingFilters searchingFilters) {
        String sql = "";
        if (searchingFilters.hasBy(CategoryName.GENERATION)) {
            sql += "JOIN study_filter sf1 ON s.id = sf1.study_id "
                    + "JOIN filter gen_filter ON sf1.filter_id = gen_filter.id "
                    + "JOIN category c1 ON gen_filter.category_id = c1.id AND c1.name = '" + CategoryName.GENERATION
                    + "' ";
        }
        if (searchingFilters.hasBy(CategoryName.AREA)) {
            sql += "JOIN study_filter sf2 ON s.id = sf2.study_id "
                    + "JOIN filter area_filter ON sf2.filter_id = area_filter.id "
                    + "JOIN category c2 ON area_filter.category_id = c2.id AND c2.name = '" + CategoryName.AREA + "' ";
        }
        if (searchingFilters.hasBy(CategoryName.TAG)) {
            sql += "JOIN study_filter sf3 ON s.id = sf3.study_id "
                    + "JOIN filter tag_filter ON sf3.filter_id = tag_filter.id "
                    + "JOIN category c3 ON tag_filter.category_id = c3.id AND c3.name = '" + CategoryName.TAG + "' ";
        }
        return sql;
    }

    private String filtersInQueryClause(final SearchingFilters searchingFilters) {
        String sql = "";
        if (searchingFilters.hasBy(CategoryName.GENERATION)) {
            sql += "AND gen_filter.id IN (:generationIds) ";
        }
        if (searchingFilters.hasBy(CategoryName.AREA)) {
            sql += "AND area_filter.id IN (:areaIds) ";
        }
        if (searchingFilters.hasBy(CategoryName.TAG)) {
            sql += "AND tag_filter.id IN (:tagIds) ";
        }
        return sql;
    }

    private Map<String, Object> params(final String title, final SearchingFilters searchingFilters,
                                       final Pageable pageable) {
        Map<String, Object> param = new HashMap<>();
        param.put("title", "%" + title + "%");
        param.put("limit", pageable.getPageSize() + 1);
        param.put("offset", pageable.getOffset());
        param.put("generationIds", searchingFilters.getFilterIdsBy(CategoryName.GENERATION));
        param.put("areaIds", searchingFilters.getFilterIdsBy(CategoryName.AREA));
        param.put("tagIds", searchingFilters.getFilterIdsBy(CategoryName.TAG));
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
