package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.services.ImportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/admin/import-export")
@RequiredArgsConstructor
@Tag(name = "Импорт/Экспорт", description = "Операции импорта и экспорта данных")
public class ImportExportController {

    private final ImportExportService importExportService;

    // ── Экспорт ───────────────────────────────────────────────────────────────

    @GetMapping("/schools/export")
    @IsAdmin
    @Operation(summary = "Экспорт школ в Excel")
    public ResponseEntity<byte[]> exportSchools() throws IOException {
        byte[] data = importExportService.exportSchools();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"schools.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/students/export")
    @IsAdmin
    @Operation(summary = "Экспорт учеников в Excel")
    public ResponseEntity<byte[]> exportStudents() throws IOException {
        byte[] data = importExportService.exportStudents();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    // ── Импорт школ ───────────────────────────────────────────────────────────

    @PostMapping("/schools/import")
    @IsAdmin
    @Operation(summary = "Импорт школ из Excel")
    public ResponseEntity<Map<String, Object>> importSchools(
            @RequestParam("file") MultipartFile file) throws IOException {
        List<String> imported = importExportService.importSchools(file);
        return ResponseEntity.ok(Map.of(
                "message", "Импорт школ завершён",
                "importedCount", imported.size(),
                "importedNames", imported
        ));
    }

    // ── Импорт учеников ───────────────────────────────────────────────────────

    @PostMapping("/students/import")
    @IsAdmin
    @Operation(summary = "Импорт учеников из Excel (ROLE_USER)")
    public ResponseEntity<Map<String, Object>> importStudents(
            @RequestParam("file") MultipartFile file) throws IOException {
        List<String> imported = importExportService.importStudents(file);
        return ResponseEntity.ok(Map.of(
                "message", "Импорт учеников завершён",
                "importedCount", imported.size(),
                "importedEmails", imported
        ));
    }

    // ── Импорт модераторов ────────────────────────────────────────────────────

    @PostMapping("/moderators/import")
    @IsAdmin
    @Operation(summary = "Импорт модераторов из Excel (ROLE_MODERATOR)",
            description = "Создаёт аккаунты с ролью ROLE_MODERATOR. " +
                    "Столбцы: A=Email, B=Никнейм, C=Имя, D=Фамилия, E=Отчество. " +
                    "Поля школа и класс не требуются для модераторов.")
    public ResponseEntity<Map<String, Object>> importModerators(
            @RequestParam("file") MultipartFile file) throws IOException {
        List<String> imported = importExportService.importModerators(file);
        return ResponseEntity.ok(Map.of(
                "message", "Импорт модераторов завершён",
                "importedCount", imported.size(),
                "importedEmails", imported
        ));
    }
}