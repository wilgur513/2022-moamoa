package com.woowacourse.learningtest;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.moamoa.MoamoaApplication;
import com.woowacourse.moamoa.NamedTriggerTask;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.SimpleTriggerContext;

@SpringBootTest(
        classes = MoamoaApplication.class
)
public class ScheduledTest {

    @Autowired
    private ScheduledAnnotationBeanPostProcessor processor;

    @Autowired
    private TriggerTask registeredTask;

    @Test
    void test1() {
        final Optional<NamedTriggerTask> namedTriggerTask = processor.getScheduledTasks().stream()
                .map(ScheduledTask::getTask)
                .filter(task -> NamedTriggerTask.class.isAssignableFrom(task.getClass()))
                .map(task -> (NamedTriggerTask) task)
                .filter(task -> task.getName().equals("triggerTask1"))
                .findAny();

        assertThat(namedTriggerTask).isPresent();

        final Trigger trigger = namedTriggerTask.get().getTrigger();
        Date date = Date.from(LocalDateTime.of(2021, 12, 3, 23, 59, 59).toInstant(ZoneOffset.of("+9")));

        final Date nextExecutionTime = trigger.nextExecutionTime(new SimpleTriggerContext(date, date, date));
        final LocalDateTime actualDateTime = nextExecutionTime.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        assertThat(actualDateTime)
                .isEqualTo(LocalDateTime.of(2021, 12, 4, 0, 0, 0));

        final Runnable runnable = namedTriggerTask.get().getRunnable();
        runnable.run();
    }

    @Test
    void test2() {
        Set<Task> tasks = processor.getScheduledTasks().stream().map(ScheduledTask::getTask).collect(Collectors.toSet());

        assertThat(tasks).contains(registeredTask);

        final Trigger trigger = registeredTask.getTrigger();
        Date date = Date.from(LocalDateTime.of(2021, 12, 3, 23, 59, 59).toInstant(ZoneOffset.of("+9")));

        final Date nextExecutionTime = trigger.nextExecutionTime(new SimpleTriggerContext(date, date, date));
        final LocalDateTime actualDateTime = nextExecutionTime.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        assertThat(actualDateTime)
                .isEqualTo(LocalDateTime.of(2021, 12, 4, 0, 0, 0));

        registeredTask.getRunnable().run();
    }
}
