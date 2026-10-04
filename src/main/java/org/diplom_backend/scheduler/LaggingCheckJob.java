package org.diplom_backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.services.LaggingCheckService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * <p>
 * Алгоритм:
 * <ol>
 *   <li>Находит все уроки TOPIC_APPROVAL с истёкшим дедлайном.</li>
 *   <li>Для каждого ученика школы, прикреплённой к такому курсу:
 *       если ученик зарегистрировался ДО дедлайна и не подал работу — помечает {@code is_lagging = true}.</li>
 *   <li>Автоматически переносит отстающих учеников в активные курсы для отстающих:
 *       удаляет их из обычных групп и создаёт индивидуальные группы в lagging-курсе.</li>
 *   <li>Логирует итог: сколько учеников изменили статус.</li>
 * </ol>
 */
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class LaggingCheckJob extends QuartzJobBean {

    private final LaggingCheckService laggingCheckService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("LaggingCheckJob: запуск ежедневной проверки отстающих учеников...");
        try {
            int changed = laggingCheckService.checkAndMarkLagging();
            log.info("LaggingCheckJob: завершено. Изменён статус {} учеников. " +
                    "Отстающие автоматически перенесены в lagging-курсы.", changed);
        } catch (Exception e) {
            log.error("LaggingCheckJob: ошибка при выполнении проверки отстающих: {}", e.getMessage(), e);
        }
    }
}