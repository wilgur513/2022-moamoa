package com.woowacourse.moamoa.filter.query.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FiltersResponse {

    private final List<FilterResponse> filters;
}
