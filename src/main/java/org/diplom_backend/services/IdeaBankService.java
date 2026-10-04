package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.CourseEntity;
import org.diplom_backend.model.IdeaBankEntry;
import org.diplom_backend.repositories.CourseRepository;
import org.diplom_backend.repositories.IdeaBankRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdeaBankService {

    private final IdeaBankRepository ideaBankRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public IdeaBankEntry create(String title, String description, String comments,
                                Integer score, Long sourceProjectId,
                                Account author) throws EntityModelNotFoundException {
        IdeaBankEntry entry = IdeaBankEntry.builder()
                .title(title)
                .description(description)
                .comments(comments)
                .score(score != null ? score : 0)
                .createdBy(author)
                .sourceProjectId(sourceProjectId)
                .build();
        return ideaBankRepository.save(entry);
    }

    /**
     * Архивирование темы с передачей собранных комментариев из hearing reviews.
     */
    @Transactional
    public IdeaBankEntry archiveFromProject(String oldTitle, String oldDescription,
                                            String commentsFromReviews,
                                            Integer score, Long groupId,
                                            Account actor) {
        IdeaBankEntry entry = IdeaBankEntry.builder()
                .title(oldTitle)
                .description(oldDescription)
                .comments(commentsFromReviews)
                .score(score != null ? score : 0)
                .createdBy(actor)
                .sourceProjectId(groupId)
                .build();
        return ideaBankRepository.save(entry);
    }

    /**
     * Архивирование темы с курсом.
     */
    @Transactional
    public IdeaBankEntry archiveFromProject(String oldTitle, String oldDescription,
                                            String commentsFromReviews,
                                            Integer score, Long groupId, Long courseId,
                                            Account actor) {
        CourseEntity course = courseId != null
                ? courseRepository.findById(courseId).orElse(null)
                : null;
        IdeaBankEntry entry = IdeaBankEntry.builder()
                .title(oldTitle)
                .description(oldDescription)
                .comments(commentsFromReviews)
                .score(score != null ? score : 0)
                .createdBy(actor)
                .sourceProjectId(groupId)
                .course(course)
                .build();
        return ideaBankRepository.save(entry);
    }

    /**
     * Старый вариант без комментариев (обратная совместимость).
     */
    @Transactional
    public IdeaBankEntry archiveFromProject(String oldTitle, String oldDescription,
                                            Integer score, Long groupId,
                                            Account actor) {
        return archiveFromProject(oldTitle, oldDescription,
                "Автоматически перенесено из группы #" + groupId + " при смене темы",
                score, groupId, actor);
    }

    public Page<IdeaBankEntry> getAll(String query, Integer minScore, Integer maxScore,
                                      Long courseId, String sortBy, int page, int size) {
        Sort sort;
        if ("score_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "score").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if ("score_desc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if ("date_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return ideaBankRepository.search(query, minScore, maxScore, courseId, PageRequest.of(page, size, sort));
    }

    /** Обратная совместимость — старая сигнатура. */
    public Page<IdeaBankEntry> getAll(String query, Integer minScore, Integer maxScore,
                                      boolean sortByScoreDesc, int page, int size) {
        String sortBy = sortByScoreDesc ? "score_desc" : "date_desc";
        return getAll(query, minScore, maxScore, null, sortBy, page, size);
    }

    public IdeaBankEntry getById(Long id) {
        return ideaBankRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Идея", "id", id.toString()));
    }

    @Transactional
    public IdeaBankEntry update(Long id, String title, String description, String comments,
                                Integer score,
                                Account account) throws EntityModelNotFoundException {
        IdeaBankEntry entry = ideaBankRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Идея", "id", id.toString()));

        if (title != null && !title.isBlank()) entry.setTitle(title.trim());
        if (description != null) entry.setDescription(description.trim());
        if (comments != null) entry.setComments(comments.trim());
        if (score != null) entry.setScore(score);

        return ideaBankRepository.save(entry);
    }

    @Transactional
    public void delete(Long id, Account account) throws EntityModelNotFoundException {
        IdeaBankEntry entry = ideaBankRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Идея", "id", id.toString()));
        ideaBankRepository.delete(entry);
    }
}