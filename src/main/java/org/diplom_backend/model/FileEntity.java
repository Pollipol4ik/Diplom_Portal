package org.diplom_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Вложение (файл)
 */
@Entity
@Table(name = "file")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileEntity {
    /**
     * Идентификатор вложения (файла)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ссылка на вложение (файл)
     */
    @Column(name = "file_name_in_directory")
    private String fileNameInDirectory;

    /**
     * Расширение вложения (файла)
     */
    @Column(name = "initial_file_name")
    private String initialFileName;
}
