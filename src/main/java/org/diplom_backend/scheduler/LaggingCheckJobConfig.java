package org.diplom_backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LaggingCheckJobConfig {
    private final SchedulerProperties schedulerProperties;

    @Bean
    public JobDetail laggingCheckJobDetail() {
        return JobBuilder.newJob(LaggingCheckJob.class)
                .withIdentity("laggingCheckJob", schedulerProperties.getPermanentJobsGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    public Trigger laggingCheckTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(laggingCheckJobDetail())
                .withIdentity("laggingCheckJobTrigger", schedulerProperties.getPermanentJobsGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(schedulerProperties.getLaggingCheckJobCron()))
                .build();
    }
}
