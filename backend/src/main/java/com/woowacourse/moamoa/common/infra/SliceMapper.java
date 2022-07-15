package com.woowacourse.moamoa.common.infra;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

public class SliceMapper {

    public <T> SliceImpl<T> mapToSlice(final List<T> content, final Pageable pageable) {
        return new SliceImpl<>(currentSliceContent(content, pageable), pageable, hasNext(content, pageable));
    }

    private <T> List<T> currentSliceContent(final List<T> content, final Pageable pageable) {
        if (hasNext(content, pageable)) {
            return content.subList(0, content.size() - 1);
        }
        return content;
    }

    private boolean hasNext(final List<?> content, final Pageable pageable) {
        return content.size() > pageable.getPageSize();
    }
}
