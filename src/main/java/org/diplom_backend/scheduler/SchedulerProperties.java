package org.diplom_backend.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerProperties {
    private String permanentJobsGroupName;
    private String deleteCommentJobCron;
    private String laggingCheckJobCron;
}
