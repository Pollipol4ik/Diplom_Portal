package org.diplom_backend.services;


import org.diplom_backend.model.FileEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init();

    FileEntity store(MultipartFile file);

    Resource loadAsResource(String filename);

    String getInitialFileName(String filename);

    void delete(String filename);

}
