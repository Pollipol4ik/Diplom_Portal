package org.diplom_backend.services;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.scheduler.SchedulerProperties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final Scheduler scheduler;
    private final SchedulerProperties schedulerProperties;
    private final LaggingCheckService laggingCheckService;
    private final CommentService commentService;

    /**
     * Установить расписание задачи удаления устаревших комментариев.
     */
    public String setVerificationTime(String cron) throws SchedulerException, ParseException {
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(
                TriggerKey.triggerKey("deleteCommentJobTrigger", schedulerProperties.getPermanentJobsGroupName())
        );
        trigger.setCronExpression(cron);
        scheduler.rescheduleJob(trigger.getKey(), trigger);
        return cron;
    }

    /**
     * Запустить удаление устаревших комментариев вручную немедленно.
     */
    public void runDeleteCommentsNow() {
        commentService.deleteOldComment();
    }

    /**
     * Установить расписание задачи проверки отстающих учеников.
     */
    public String setLaggingCheckTime(String cron) throws SchedulerException, ParseException {
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(
                TriggerKey.triggerKey("laggingCheckJobTrigger", schedulerProperties.getPermanentJobsGroupName())
        );
        trigger.setCronExpression(cron);
        scheduler.rescheduleJob(trigger.getKey(), trigger);
        return cron;
    }

    /**
     * Запустить проверку отстающих вручную немедленно.
     */
    @Transactional
    public int runLaggingCheckNow() {
        return laggingCheckService.checkAndMarkLagging();
    }
}