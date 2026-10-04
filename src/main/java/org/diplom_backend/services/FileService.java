package org.diplom_backend.services;


import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.config.StorageProperties;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.StorageException;
import org.diplom_backend.exceptions.StorageFileNotFoundException;
import org.diplom_backend.model.FileEntity;
import org.diplom_backend.repositories.FileRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements StorageService {
    private final StorageProperties properties;
    private final MinioClient client;
    private final FileRepository fileRepository;

    @Override
    @SneakyThrows
    public FileEntity store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            UUID fileId = UUID.randomUUID();
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object((fileId + file.getOriginalFilename()).toString())
                            .stream(new ByteArrayInputStream(file.getBytes()),
                                    file.getSize(),
                                    properties.getImageSize())
                            .contentType(file.getContentType())
                            .build()
            );
            return new FileEntity(null, fileId + file.getOriginalFilename(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try (InputStream inputStream = client.getObject(
                GetObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(filename)
                        .build())) {

            byte[] bytes = inputStream.readAllBytes();
            if (bytes.length == 0) {
                throw new StorageFileNotFoundException("Файл: " + filename + " пустой или не найден");
            }

            return new ByteArrayResource(bytes);

        } catch (Exception e) {
            throw new StorageFileNotFoundException("Невозможно прочитать файл: " + filename, e);
        }
    }

    @Override
    @SneakyThrows
    @PostConstruct
    public void init() {
        var bucketName = properties.getBucket();
        if (Objects.isNull(bucketName) || bucketName.isBlank()) {
            throw new StorageException("You should specify bucket name to use storage");
        }
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            client.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
        }
    }

    @Override
    public String getInitialFileName(String filename) {
        FileEntity file = fileRepository.findByFileNameInDirectory(filename).orElseThrow(
                () -> new EntityModelNotFoundException("Файла", "именем", filename)
        );
        return file.getInitialFileName();
    }
    @Override
    @SneakyThrows
    public void delete(String filename) {
        try {
            client.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(filename)
                            .build()
            );
            fileRepository.findByFileNameInDirectory(filename).ifPresent(fileRepository::delete);
        } catch (Exception e) {
            log.warn("Не удалось удалить файл {}: {}", filename, e.getMessage());
        }
    }
}
