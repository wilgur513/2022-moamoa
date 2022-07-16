package com.woowacourse.moamoa.filter.query.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class FilterResponse {

    private final Long id;
    private final String name;
    private final CategoryResponse category;
}
