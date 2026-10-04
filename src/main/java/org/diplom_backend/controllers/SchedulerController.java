package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.SetVerificationTimeRequestDto;
import org.diplom_backend.dto.responses.SetVerificationTimeResponseDto;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.services.LaggingCheckService;
import org.diplom_backend.services.SchedulerService;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Планировщик", description = "Работа с планировщиком")
@RequestMapping("/v1/scheduler")
@IsAdmin
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final LaggingCheckService laggingCheckService;

    /** POST /v1/scheduler — установить cron удаления комментариев */
    @PostMapping
    @Operation(summary = "Установить расписание удаления комментариев")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Расписание обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректный cron"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
    })
    public SetVerificationTimeResponseDto setVerificationTime(
            @RequestBody @Valid SetVerificationTimeRequestDto request
    ) throws SchedulerException, ParseException {
        return new SetVerificationTimeResponseDto(schedulerService.setVerificationTime(request.cron()));
    }

    /** POST /v1/scheduler/run-comments — немедленно запустить удаление комментариев */
    @PostMapping("/run-comments")
    @Operation(summary = "Немедленно запустить удаление устаревших комментариев")
    @ApiResponse(responseCode = "200", description = "Задача выполнена")
    public Map<String, String> runDeleteCommentsNow() {
        schedulerService.runDeleteCommentsNow();
        return Map.of("message", "Удаление устаревших комментариев выполнено");
    }

    /** POST /v1/scheduler/lagging-check — установить cron проверки отстающих */
    @PostMapping("/lagging-check")
    @Operation(summary = "Установить расписание проверки отстающих учеников")
    @ApiResponse(responseCode = "200", description = "Расписание обновлено")
    public SetVerificationTimeResponseDto setLaggingCheckTime(
            @RequestBody @Valid SetVerificationTimeRequestDto request
    ) throws SchedulerException, ParseException {
        return new SetVerificationTimeResponseDto(schedulerService.setLaggingCheckTime(request.cron()));
    }

    /** POST /v1/scheduler/run-lagging-check — немедленно запустить проверку отстающих */
    @PostMapping("/run-lagging-check")
    @Operation(summary = "Немедленно запустить проверку отстающих")
    @ApiResponse(responseCode = "200", description = "Проверка выполнена")
    public Map<String, Object> runLaggingCheckNow() {
        int marked = laggingCheckService.checkAndMarkLagging();
        return Map.of(
                "marked", marked,
                "message", "Проверка завершена. Новых отстающих помечено: " + marked
        );
    }
}