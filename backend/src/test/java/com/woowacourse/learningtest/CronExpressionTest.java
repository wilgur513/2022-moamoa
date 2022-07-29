package com.woowacourse.learningtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.scheduling.support.CronExpression;

public class CronExpressionTest {


    @ParameterizedTest
    @MethodSource("provideExpectAndActualDate")
    void findNextScheduledDate(LocalDateTime actual, LocalDateTime expect) {
        final CronExpression expression = CronExpression.parse("@daily");

        assertThat(expression.next(actual)).isEqualTo(expect);
    }

    private static Stream<Arguments> provideExpectAndActualDate() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2021, 12, 31, 23, 59, 59),
                        LocalDateTime.of(2022, 1, 1, 0, 0, 0)),
                Arguments.of(LocalDateTime.of(2021, 1, 23, 0, 0, 1),
                        LocalDateTime.of(2021, 1, 24, 0, 0, 0))
        );
    }
}
