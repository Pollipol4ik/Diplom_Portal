package org.diplom_backend.services;


import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.FileEntity;
import org.diplom_backend.model.NewsPublicationEntity;
import org.diplom_backend.model.PublicationEntity;
import org.diplom_backend.repositories.NewsPublicationRepository;
import org.diplom_backend.repositories.PublicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsPublicationService {
    private final NewsPublicationRepository newsPublicationRepository;
    private final PublicationRepository publicationRepository;
    private final FileService fileService;
    private final TelegramNotificationService notificationService;

    @Transactional
    public PublicationEntity createPublicationInNews(PublicationEntity publication, Account account, List<MultipartFile> files) throws EntityModelNotFoundException {
        if (files != null) {
            var filesInPublication = new HashSet<FileEntity>();
            for (var file : files) {
                var fileInPublication = fileService.store(file);
                filesInPublication.add(fileInPublication);
            }
            publication.setFiles(filesInPublication);
        } else {
            publication.setFiles(new HashSet<>());
        }
        publication.setSupportsThread(true);
        publication.setAccount(account);
        var savedPublication = publicationRepository.save(publication);
        newsPublicationRepository.save(new NewsPublicationEntity(savedPublication.getId(), savedPublication));
        notificationService.notifyNewsSubscribersAsync(
                publication.getTitle(),
                publication.getDescription());
        return savedPublication;
    }

    public Page<PublicationEntity> getPublicationsInNews(Integer pageNumber, Integer pageSize) {
        return newsPublicationRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id").reverse())).map(NewsPublicationEntity::getPublication);
    }
}
