package com.woowacourse.moamoa.filter.query;

import com.woowacourse.moamoa.filter.domain.CategoryId;
import com.woowacourse.moamoa.filter.query.response.CategoryResponse;
import com.woowacourse.moamoa.filter.query.response.FilterResponse;
import com.woowacourse.moamoa.filter.query.response.FiltersResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FiltersSearcher {

    public static final RowMapper<FilterResponse> ROW_MAPPER = (rs, rn) -> {
        final long filterId = rs.getLong("filter_id");
        final String filterName = rs.getString("filter_name");
        final long categoryId = rs.getLong("category_id");
        final String categoryName = rs.getString("category_name");

        return new FilterResponse(filterId, filterName, new CategoryResponse(categoryId, categoryName));
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FiltersResponse queryBy(final String name, final CategoryId categoryId) {
        final List<FilterResponse> filterResponses = jdbcTemplate
                .query(sql(categoryId), param(name, categoryId), ROW_MAPPER);
        return new FiltersResponse(filterResponses);
    }

    private Map<String, Object> param(final String name, final CategoryId categoryId) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", "%" + name + "%");
        param.put("categoryId", categoryId.getValue());
        return param;
    }

    private String sql(final CategoryId categoryId) {
        return "SELECT f.id as filter_id, f.name as filter_name, "
                + "c.id as category_id, c.name as category_name "
                + "FROM filter as f JOIN category as c ON f.category_id = c.id "
                + "WHERE UPPER(f.name) LIKE UPPER(:name) " + AND_c_id_EQ_category_id(categoryId);
    }

    private String AND_c_id_EQ_category_id(final CategoryId categoryId) {
        return categoryId.isEmpty() ? "" : "AND c.id = :categoryId";
    }
}
