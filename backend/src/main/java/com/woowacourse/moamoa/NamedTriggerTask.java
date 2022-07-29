package com.woowacourse.moamoa;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.TriggerTask;

public class NamedTriggerTask extends TriggerTask {

    private final String name;

    public NamedTriggerTask(final String name, final Runnable runnable, final Trigger trigger) {
        super(runnable, trigger);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
