package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.diplom_backend.model.RoleEntity;
import org.diplom_backend.model.School;
import org.diplom_backend.model.SchoolClass;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.RoleRepository;
import org.diplom_backend.repositories.SchoolClassRepository;
import org.diplom_backend.repositories.SchoolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportService {

    private final SchoolRepository schoolRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ── ЭКСПОРТ ───────────────────────────────────────────────────────────────

    public byte[] exportSchools() throws IOException {
        List<School> schools = schoolRepository.findAll();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Школы");
            CellStyle headerStyle = createHeaderStyle(wb);

            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Название"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (School school : schools) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(school.getId());
                row.createCell(1).setCellValue(safeStr(school.getName()));
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportStudents() throws IOException {
        List<Account> students = accountRepository.findAll().stream()
                .filter(a -> a.getRole() != null && a.getRole().getName() == Role.ROLE_USER)
                .toList();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Ученики");
            CellStyle headerStyle = createHeaderStyle(wb);

            String[] headers = {"ID", "Email", "Никнейм", "Имя", "Фамилия", "Отчество",
                    "Школа", "Класс", "Заблокирован", "Отстающий"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Account a : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(a.getId());
                row.createCell(1).setCellValue(safeStr(a.getEmail()));
                row.createCell(2).setCellValue(safeStr(a.getNickname()));
                row.createCell(3).setCellValue(safeStr(a.getFirstName()));
                row.createCell(4).setCellValue(safeStr(a.getLastName()));
                row.createCell(5).setCellValue(safeStr(a.getMiddleName()));
                row.createCell(6).setCellValue(a.getSchool() != null ? a.getSchool().getName() : "");
                row.createCell(7).setCellValue(a.getSchoolClass() != null ? a.getSchoolClass().getName() : "");
                row.createCell(8).setCellValue(Boolean.TRUE.equals(a.getIsBanned()) ? "Да" : "Нет");
                row.createCell(9).setCellValue(Boolean.TRUE.equals(a.getIsLagging()) ? "Да" : "Нет");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    // ── ИМПОРТ ШКОЛ ──────────────────────────────────────────────────────────

    @Transactional
    public List<String> importSchools(MultipartFile file) throws IOException {
        List<String> imported = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String name = getCellString(row, 0);
                if (name.isBlank()) continue;
                if (schoolRepository.findByName(name).isEmpty()) {
                    School school = new School();
                    school.setName(name);
                    schoolRepository.save(school);
                    imported.add(name);
                    log.info("Импортирована школа: {}", name);
                }
            }
        }
        return imported;
    }

    // ── ИМПОРТ УЧЕНИКОВ ──────────────────────────────────────────────────────

    /**
     * Импортирует учеников из Excel-файла.
     *
     * <p>Поддерживает ДВА формата:
     *
     * <p><b>Формат A — многолистовой (наш формат экспорта)</b>:
     * Название листа = название школы (пропускается лист "Сводка").
     * Строка 1: заголовок листа (школа), строка 2: шапка столбцов, строки 3+: данные.
     * Столбцы: №(0), Фамилия(1), Имя(2), Отчество(3), Пол(4), Класс(5), Email(6), Ник(7).
     *
     * <p><b>Формат B — однолистовой (старый формат)</b>:
     * Строка 1: шапка, строки 2+: данные.
     * Столбцы: Email(0), Ник(1), Имя(2), Фамилия(3), Школа(4), Класс(5).
     *
     * <p>Классы создаются автоматически если не существуют в школе.
     * Ник генерируется уникальным при коллизиях (добавляется числовой суффикс).
     * Аккаунты с уже существующим email пропускаются.
     *
     * @return список импортированных email-адресов
     */
    // ── ИМПОРТ УЧЕНИКОВ ──────────────────────────────────────────────────────

    @Transactional
    public List<String> importStudents(MultipartFile file) throws IOException {
        List<String> imported = new ArrayList<>();
        RoleEntity userRole = roleRepository.findByName(Role.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена в базе"));

        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            boolean isMultiSheet = wb.getNumberOfSheets() > 1
                    || isMultiSheetFormat(wb.getSheetAt(0));

            if (isMultiSheet) {
                // ── Формат A: многолистовой ──────────────────────────────────
                for (int si = 0; si < wb.getNumberOfSheets(); si++) {
                    Sheet sheet = wb.getSheetAt(si);
                    String sheetName = sheet.getSheetName().trim();

                    if (sheetName.equalsIgnoreCase("Сводка")
                            || sheetName.equalsIgnoreCase("Summary")
                            || sheetName.isBlank()) {
                        continue;
                    }

                    School school = findOrSkipSchool(sheetName);
                    final Map<String, SchoolClass> classCache = buildClassCache(school); // ← final

                    int dataStartRow = detectDataStartRow(sheet);
                    for (int i = dataStartRow; i <= sheet.getLastRowNum(); i++) {
                        Row row = sheet.getRow(i);
                        if (row == null) continue;

                        String email     = getCellString(row, 6);
                        String nickname  = getCellString(row, 7);
                        String lastName  = getCellString(row, 1);
                        String firstName = getCellString(row, 2);
                        String middleName = getCellString(row, 3);
                        String className = getCellString(row, 5);

                        imported.addAll(importOneStudent(email, nickname, firstName, lastName,
                                middleName, className, school, classCache, userRole));
                    }
                }
            } else {
                // ── Формат B: однолистовой ──────────────────────────────────
                Sheet sheet = wb.getSheetAt(0);
                // Кэш школ и классов
                Map<String, School> schoolCache = new HashMap<>();
                Map<Long, Map<String, SchoolClass>> classCache2 = new HashMap<>();

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String email      = getCellString(row, 0);
                    String nickname   = getCellString(row, 1);
                    String firstName  = getCellString(row, 2);
                    String lastName   = getCellString(row, 3);
                    String schoolName = getCellString(row, 4);
                    String className  = getCellString(row, 5);
                    String middleName = ""; // В формате B нет отчества

                    final School school;
                    if (!schoolName.isBlank()) {
                        School foundSchool = schoolCache.get(schoolName);
                        if (foundSchool == null) {
                            foundSchool = schoolRepository.findByName(schoolName).orElse(null);
                            if (foundSchool != null) {
                                schoolCache.put(schoolName, foundSchool);
                            }
                        }
                        school = foundSchool;
                    } else {
                        school = null;
                    }

                    Map<String, SchoolClass> classCache;
                    if (school != null) {
                        if (classCache2.containsKey(school.getId())) {
                            classCache = classCache2.get(school.getId());
                        } else {
                            classCache = buildClassCache(school);
                            classCache2.put(school.getId(), classCache);
                        }
                    } else {
                        classCache = new HashMap<>();
                    }

                    imported.addAll(importOneStudent(email, nickname, firstName, lastName,
                            middleName, className, school, classCache, userRole));
                }
            }
        }

        return imported;
    }

    // ── ИМПОРТ МОДЕРАТОРОВ ────────────────────────────────────────────────────

    @Transactional
    public List<String> importModerators(MultipartFile file) throws IOException {
        List<String> imported = new ArrayList<>();
        RoleEntity moderatorRole = roleRepository.findByName(Role.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Роль ROLE_MODERATOR не найдена в базе"));

        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String email = getCellString(row, 0);
                if (email.isBlank()) continue;
                if (accountRepository.existsByEmail(email)) continue;

                String nickname   = getCellString(row, 1);
                String firstName  = getCellString(row, 2);
                String lastName   = getCellString(row, 3);
                String middleName = getCellString(row, 4);

                Account account = new Account();
                account.setEmail(email);
                account.setNickname(resolveUniqueNickname(
                        nickname.isBlank() ? generateNickname(firstName, lastName, email) : nickname));
                account.setFirstName(firstName.isBlank() ? null : firstName);
                account.setLastName(lastName.isBlank() ? null : lastName);
                account.setMiddleName(middleName.isBlank() ? null : middleName);
                account.setPassword(passwordEncoder.encode("ChangeMe123!"));
                account.setIsBanned(false);
                account.setIsLagging(false);
                account.setRole(moderatorRole);

                accountRepository.save(account);
                imported.add(email);
                log.info("Импортирован модератор: {}", email);
            }
        }
        return imported;
    }

    // ── ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ────────────────────────────────────────────────

    /**
     * Создаёт или обновляет одного ученика. Возвращает список импортированных email (0 или 1).
     */
    private List<String> importOneStudent(String email, String nickname,
                                          String firstName, String lastName, String middleName,
                                          String className, School school,
                                          Map<String, SchoolClass> classCache,
                                          RoleEntity userRole) {
        if (email.isBlank()) return List.of();
        if (accountRepository.existsByEmail(email)) {
            log.debug("Аккаунт уже существует, пропускается: {}", email);
            return List.of();
        }

        Account account = new Account();
        account.setEmail(email);
        account.setFirstName(firstName.isBlank() ? null : firstName.trim());
        account.setLastName(lastName.isBlank() ? null : lastName.trim());
        account.setMiddleName(middleName.isBlank() ? null : middleName.trim());
        account.setPassword(passwordEncoder.encode("ChangeMe123!"));
        account.setIsBanned(false);
        account.setIsLagging(false);
        account.setRole(userRole);
        account.setSchool(school);

        String base = nickname.isBlank()
                ? generateNickname(firstName, lastName, email)
                : nickname.trim();
        account.setNickname(resolveUniqueNickname(base));

        if (!className.isBlank() && school != null) {
            String classKey = className.trim().toLowerCase();
            SchoolClass schoolClass = classCache.computeIfAbsent(classKey, k -> {
                return schoolClassRepository.findAllBySchoolId(school.getId()).stream()
                        .filter(c -> c.getName().equalsIgnoreCase(className.trim()))
                        .findFirst()
                        .orElseGet(() -> {
                            SchoolClass newClass = new SchoolClass();
                            newClass.setName(className.trim());
                            newClass.setSchool(school);
                            SchoolClass saved = schoolClassRepository.save(newClass);
                            log.info("Автоматически создан класс '{}' для школы '{}'",
                                    className.trim(), school.getName());
                            return saved;
                        });
            });
            account.setSchoolClass(schoolClass);
        }

        accountRepository.save(account);
        log.info("Импортирован ученик: {} (ник: {})", email, account.getNickname());
        return List.of(email);
    }

    /**
     * Определяет строку начала данных в листе.
     * Если строка 0 содержит объединённые ячейки (заголовок листа) — данные начинаются с row 2.
     * Иначе данные начинаются с row 1 (строка 0 — шапка).
     */
    private int detectDataStartRow(Sheet sheet) {
        Row row0 = sheet.getRow(0);
        if (row0 != null) {
            String val = getCellString(row0, 0);
            if (val.toLowerCase().contains("список") || val.toLowerCase().contains("школ")) {
                return 2;
            }
        }
        return 1;
    }

    /**
     * Определяет, является ли файл многолистовым по формату.
     * Для одного листа проверяем что email находится в столбце 6 (наш формат), а не 0 (старый).
     */
    private boolean isMultiSheetFormat(Sheet sheet) {
        for (int i = 1; i <= Math.min(5, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String col6 = getCellString(row, 6);
            if (col6.contains("@")) return true; // email в столбце 6 = наш формат
        }
        return false;
    }

    /** Находит школу по названию листа. Если не найдена — создаёт её. */
    private School findOrSkipSchool(String sheetName) {
        return schoolRepository.findByName(sheetName)
                .orElseGet(() -> {
                    School school = new School();
                    school.setName(sheetName);
                    School saved = schoolRepository.save(school);
                    log.info("Автоматически создана школа: {}", sheetName);
                    return saved;
                });
    }

    /** Строит кэш классов школы (ключ — lowercase название класса). */
    private Map<String, SchoolClass> buildClassCache(School school) {
        if (school == null) return new HashMap<>();
        Map<String, SchoolClass> cache = new HashMap<>();
        schoolClassRepository.findAllBySchoolId(school.getId())
                .forEach(c -> cache.put(c.getName().toLowerCase(), c));
        return cache;
    }

    /**
     * Генерирует базовый никнейм из имени/фамилии или email.
     * Формат: первая буква имени + точка + фамилия в нижнем регистре (транслитерация).
     */
    private String generateNickname(String firstName, String lastName, String email) {
        if (!firstName.isBlank() && !lastName.isBlank()) {
            String transFirst = transliterate(firstName.trim());
            String transLast = transliterate(lastName.trim());
            if (!transFirst.isBlank() && !transLast.isBlank()) {
                return (transFirst.charAt(0) + "." + transLast).toLowerCase();
            }
        }
        // Запасной вариант — часть email до @
        int atIdx = email.indexOf('@');
        return atIdx > 0 ? email.substring(0, atIdx).toLowerCase() : email.toLowerCase();
    }

    /**
     * Возвращает уникальный никнейм: если base уже занят, добавляет суффикс _2, _3, ...
     */
    private String resolveUniqueNickname(String base) {
        String clean = base.replaceAll("[^a-zA-Z0-9._\\-]", "_");
        if (accountRepository.findByNickname(clean).isEmpty()) {
            return clean;
        }
        for (int i = 2; i <= 9999; i++) {
            String candidate = clean + "_" + i;
            if (accountRepository.findByNickname(candidate).isEmpty()) {
                return candidate;
            }
        }
        return clean + "_" + System.currentTimeMillis();
    }

    /** Простая транслитерация кириллицы → латиница. */
    private String transliterate(String input) {
        Map<Character, String> map = new HashMap<>();
        String[][] pairs = {
                {"а","a"},{"б","b"},{"в","v"},{"г","g"},{"д","d"},{"е","e"},{"ё","yo"},
                {"ж","zh"},{"з","z"},{"и","i"},{"й","j"},{"к","k"},{"л","l"},{"м","m"},
                {"н","n"},{"о","o"},{"п","p"},{"р","r"},{"с","s"},{"т","t"},{"у","u"},
                {"ф","f"},{"х","kh"},{"ц","ts"},{"ч","ch"},{"ш","sh"},{"щ","sch"},
                {"ъ",""},{"ы","y"},{"ь",""},{"э","e"},{"ю","yu"},{"я","ya"}
        };
        for (String[] p : pairs) {
            map.put(p[0].charAt(0), p[1]);
            map.put(Character.toUpperCase(p[0].charAt(0)), p[1]);
        }
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            sb.append(map.getOrDefault(c, String.valueOf(c)));
        }
        return sb.toString();
    }

    // ── Стили и хелперы ──────────────────────────────────────────────────────

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private String safeStr(String value) {
        return value != null ? value : "";
    }
}