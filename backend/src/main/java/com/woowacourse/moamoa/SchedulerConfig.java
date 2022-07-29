package com.woowacourse.moamoa;

import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(task1());
    }

    @Bean
    public TriggerTask task1() {
        return new NamedTriggerTask("triggerTask1",
                () -> {
                    System.out.println("Hello Trigger Task1");
                },
                new CronTrigger("0 0 0 * * *", ZoneId.of("Asia/Seoul")));
    }
}
