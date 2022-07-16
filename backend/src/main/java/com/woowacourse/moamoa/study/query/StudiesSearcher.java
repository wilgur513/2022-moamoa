package com.woowacourse.moamoa.study.query;

import com.woowacourse.moamoa.filter.domain.CategoryName;
import com.woowacourse.moamoa.study.query.response.StudiesResponse;
import com.woowacourse.moamoa.study.query.response.StudyResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        String sql = "JOIN study_filter {}_sf ON s.id = {}_sf.study_id "
                + "JOIN filter {}_f ON {}_sf.filter_id = {}_f.id "
                + "JOIN category {}_c ON {}_f.category_id = {}_c.id AND {}_c.name = '{}'";

        return Stream.of(CategoryName.values())
                .filter(searchingFilters::hasBy)
                .map(name -> sql.replaceAll("\\{\\}", name.name()))
                .collect(Collectors.joining());
    }

    private String filtersInQueryClause(final SearchingFilters searchingFilters) {
        String sql = "AND {}_f.id IN (:{}) ";

        return Stream.of(CategoryName.values())
                .filter(searchingFilters::hasBy)
                .map(name -> sql.replaceAll("\\{\\}", name.name()))
                .collect(Collectors.joining());
    }

    private Map<String, Object> params(final String title, final SearchingFilters searchingFilters,
                                       final Pageable pageable) {
        final Map<String, Object> filterIds = Stream.of(CategoryName.values())
                .collect(Collectors.toMap(Enum::name, searchingFilters::getFilterIdsBy));

        Map<String, Object> param = new HashMap<>();
        param.put("title", "%" + title + "%");
        param.put("limit", pageable.getPageSize() + 1);
        param.put("offset", pageable.getOffset());
        param.putAll(filterIds);
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
