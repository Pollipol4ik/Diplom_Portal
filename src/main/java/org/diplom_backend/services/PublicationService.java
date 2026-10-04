package org.diplom_backend.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.FileEntity;
import org.diplom_backend.model.PublicationEntity;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
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
@Slf4j
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final SubjectTopicService subjectTopicService;
    private final FileService fileService;
    private final AccessControlService accessControlService;
    private final TelegramNotificationService notificationService;

    @Transactional
    public PublicationEntity createPublicationInSubjectTopic(PublicationEntity publication,
                                                             Account account,
                                                             List<MultipartFile> files)
            throws EntityModelNotFoundException {

        Long subjectTopicId = publication.getSubjectTopics().iterator().next().getId();
        SubjectTopicEntity subjectTopicEntity = subjectTopicService.getSubjectTopic(subjectTopicId);

        accessControlService.verifyModeratorAccess(account, subjectTopicEntity.getSubject().getId());

        if (files != null && !files.isEmpty()) {
            var filesInPublication = new HashSet<FileEntity>();
            for (var file : files) {
                var fileInPublication = fileService.store(file);
                filesInPublication.add(fileInPublication);
            }
            publication.setFiles(filesInPublication);
        } else {
            publication.setFiles(new HashSet<>());
        }

        var subjectTopic = new HashSet<SubjectTopicEntity>();
        subjectTopic.add(subjectTopicEntity);

        publication.setSubjectTopics(subjectTopic);
        publication.setSupportsThread(false);
        publication.setAccount(account);

        PublicationEntity savedPublication = publicationRepository.save(publication);

        try {
            Long courseNumber = null;
            Long subjectId = null;
            Long topicId = subjectTopicEntity.getId();
            String subjectName = null;
            String topicName = null;
            String authorNickname = account != null ? account.getNickname() : null;

            if (subjectTopicEntity.getSubject() != null) {
                SubjectEntity subject = subjectTopicEntity.getSubject();
                subjectId = subject.getId();
                subjectName = subject.getName();

                if (subject.getDirection() != null) {
                    courseNumber = subject.getDirection().getId();
                }
            }

            topicName = (subjectTopicEntity.getName() != null)
                    ? subjectTopicEntity.getName()
                    : "Тема";

            log.info("Отправка уведомлений о публикации '{}': курс {}, предмет '{}', тема '{}'",
                    savedPublication.getTitle(), courseNumber, subjectName, topicName);

            notificationService.notifyPublicationSubscribers(
                    courseNumber,
                    subjectId,
                    topicId,
                    subjectName,
                    topicName,
                    savedPublication.getTitle(),
                    savedPublication.getDescription(),
                    authorNickname
            );

        } catch (Exception e) {
            log.error("Ошибка при отправке уведомлений о публикации '{}': {}",
                    savedPublication.getTitle(), e.getMessage(), e);
        }

        return savedPublication;
    }

    public PublicationEntity getPublication(Long id) throws EntityModelNotFoundException {
        return publicationRepository.findById(id).orElseThrow(() -> new EntityModelNotFoundException("Публикации", "id", Long.toString(id)));
    }

    @Transactional
    public void deletePublication(Long id, Account currentAccount) throws EntityModelNotFoundException {
        PublicationEntity publication = getPublication(id);

        Long subjectId = publication.getSubjectTopics().stream()
                .findFirst()
                .map(st -> st.getSubject().getId())
                .orElseThrow(() -> new IllegalStateException("Публикация не привязана к предмету"));

        accessControlService.verifyModeratorAccess(currentAccount, subjectId);
        publicationRepository.delete(publication);
    }

    public Page<PublicationEntity> getPublicationsInOneCategory(Integer pageNumber, Integer pageSize, Long subjectTopicId) {
        return publicationRepository.findBySubjectTopics_Id(subjectTopicId, PageRequest.of(pageNumber, pageSize, Sort.by("id")));
    }
}