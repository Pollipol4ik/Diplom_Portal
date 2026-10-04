package org.diplom_backend.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.exceptions.AlreadySubscribedException;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
import org.diplom_backend.model.UserSubscriptionEntity;
import org.diplom_backend.model.UserSubscriptionEntity.SubscriptionType;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.UserSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления подписками пользователей
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;
    private final AccountRepository accountRepository;
    private final DirectionService courseService;
    private final SubjectService subjectService;
    private final SubjectTopicService subjectTopicService;
    private final AccountService accountService;

    /**
     * Подписаться на новости
     */
    @Transactional
    public UserSubscriptionEntity subscribeToNews(Long accountId) throws EntityModelNotFoundException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", accountId.toString()));

        Optional<UserSubscriptionEntity> existing = subscriptionRepository
                .findByAccountIdAndTypeAndIsActiveTrue(accountId, SubscriptionType.NEWS);

        if (existing.isPresent()) {
            log.info("User {} already subscribed to news", accountId);
            throw new AlreadySubscribedException("Уже подписан на новости");
        }

        UserSubscriptionEntity subscription = new UserSubscriptionEntity();
        subscription.setAccount(account);
        subscription.setType(SubscriptionType.NEWS);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setIsActive(true);

        log.info("User {} subscribed to news", accountId);
        return subscriptionRepository.save(subscription);
    }


    /**
     * Подписаться на курс
     */
    @Transactional
    public UserSubscriptionEntity subscribeToCourse(Long accountId, Long courseNumber)
            throws EntityModelNotFoundException {

        courseService.getDirectionById(courseNumber);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", accountId.toString()));

        Optional<UserSubscriptionEntity> existing = subscriptionRepository
                .findByAccountIdAndTypeAndDirectionIdAndIsActiveTrue(
                        accountId, SubscriptionType.DIRECTION, courseNumber);

        if (existing.isPresent()) {
            log.info("User {} already subscribed to course {}", accountId, courseNumber);
            throw new AlreadySubscribedException("Уже подписан на курс " + courseNumber);
        }

        UserSubscriptionEntity subscription = new UserSubscriptionEntity();
        subscription.setAccount(account);
        subscription.setType(SubscriptionType.DIRECTION);
        subscription.setDirectionId(courseNumber);
        subscription.setSubjectId(null);
        subscription.setTopicId(null);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setIsActive(true);

        log.info("User {} subscribed to ENTIRE course {}", accountId, courseNumber);
        return subscriptionRepository.save(subscription);
    }


    /**
     * Подписаться на предмет
     */
    @Transactional
    public UserSubscriptionEntity subscribeToSubject(Long accountId, Long subjectId)
            throws EntityModelNotFoundException {

        SubjectEntity subject = subjectService.getSubject(subjectId);

        Long courseNumber = null;
        if (subject.getDirection() != null) {
            courseNumber = subject.getDirection().getId();
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", accountId.toString()));

        Optional<UserSubscriptionEntity> existing = subscriptionRepository
                .findByAccountIdAndTypeAndDirectionIdAndSubjectIdAndIsActiveTrue(
                        accountId, SubscriptionType.SUBJECT, courseNumber, subjectId);

        if (existing.isPresent()) {
            log.info("User {} already subscribed to subject {} (course {})",
                    accountId, subjectId, courseNumber);
            throw new AlreadySubscribedException("Уже подписан на этот предмет");
        }

        UserSubscriptionEntity subscription = new UserSubscriptionEntity();
        subscription.setAccount(account);
        subscription.setType(SubscriptionType.SUBJECT);
        subscription.setDirectionId(courseNumber);
        subscription.setSubjectId(subjectId);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setIsActive(true);

        log.info("User {} subscribed to subject {} in course {}", accountId, subjectId, courseNumber);
        return subscriptionRepository.save(subscription);
    }


    /**
     * Подписаться на тему
     */
    @Transactional
    public UserSubscriptionEntity subscribeToTopic(Long accountId, Long topicId)
            throws EntityModelNotFoundException {

        SubjectTopicEntity subjectTopic = subjectTopicService.getSubjectTopicWithRelations(topicId);

        Long subjectId = null;
        Long courseNumber = null;
        if (subjectTopic.getSubject() != null) {
            subjectId = subjectTopic.getSubject().getId();
            if (subjectTopic.getSubject().getDirection() != null) {
                courseNumber = subjectTopic.getSubject().getDirection().getId();
            }
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", accountId.toString()));

        Optional<UserSubscriptionEntity> existingAny = subscriptionRepository
                .findByAccountIdAndTypeAndDirectionIdAndSubjectIdAndTopicId(
                        accountId, SubscriptionType.TOPIC, courseNumber, subjectId, topicId);

        if (existingAny.isPresent()) {
            UserSubscriptionEntity existing = existingAny.get();
            if (existing.getIsActive()) {
                log.info("User {} already subscribed to topic {} (subject {}, course {})",
                        accountId, topicId, subjectId, courseNumber);
                throw new AlreadySubscribedException("Уже подписан на эту тему");
            } else {
                existing.setIsActive(true);
                existing.setCreatedAt(LocalDateTime.now());
                log.info("User {} reactivated subscription to topic {} (subject {}, course {})",
                        accountId, topicId, subjectId, courseNumber);
                return subscriptionRepository.save(existing);
            }
        }

        UserSubscriptionEntity subscription = new UserSubscriptionEntity();
        subscription.setAccount(account);
        subscription.setType(SubscriptionType.TOPIC);
        subscription.setDirectionId(courseNumber);
        subscription.setSubjectId(subjectId);
        subscription.setTopicId(topicId);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setIsActive(true);

        log.info("User {} subscribed to topic {} in subject {} (course {})",
                accountId, topicId, subjectId, courseNumber);
        return subscriptionRepository.save(subscription);
    }


    /**
     * Получить все подписки пользователя
     */
    @Transactional(readOnly = true)
    public List<UserSubscriptionEntity> getUserSubscriptions(Long accountId) {
        return subscriptionRepository.findByAccountIdAndIsActiveTrue(accountId);
    }

    /**
     * Отписаться от конкретной подписки (удаление из БД)
     */
    @Transactional
    public void unsubscribe(Long subscriptionId) throws EntityModelNotFoundException {
        UserSubscriptionEntity subscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() -> new EntityModelNotFoundException(
                        "Подписки", "id", subscriptionId.toString()));

        subscriptionRepository.delete(subscription);
        log.info("Subscription {} deleted from database", subscriptionId);
    }

    /**
     * Отписаться от новостей (удаление)
     */
    @Transactional
    public void unsubscribeFromNews(Long accountId) {
        subscriptionRepository
                .findByAccountIdAndTypeAndIsActiveTrue(accountId, SubscriptionType.NEWS)
                .ifPresent(sub -> {
                    subscriptionRepository.delete(sub);
                    log.info("User {} news subscription deleted", accountId);
                });
    }

    /**
     * Отписаться от курса (удаление)
     */
    @Transactional
    public void unsubscribeFromCourse(Long accountId, Long courseNumber) {
        subscriptionRepository
                .findByAccountIdAndTypeAndDirectionIdAndIsActiveTrue(
                        accountId, SubscriptionType.DIRECTION, courseNumber)
                .ifPresent(sub -> {
                    subscriptionRepository.delete(sub);
                    log.info("User {} course {} subscription deleted", accountId, courseNumber);
                });
    }
    /**
     * Получить всех подписчиков на новости
     */
    @Transactional(readOnly = true)
    public List<UserSubscriptionEntity> getNewsSubscribers() {
        return subscriptionRepository.findAllNewsSubscribers();
    }

    /**
     * Получить подписчиков на курс
     */
    @Transactional(readOnly = true)
    public List<UserSubscriptionEntity> getCourseSubscribers(Long courseNumber) {
        return subscriptionRepository.findCourseSubscribers(courseNumber);
    }

    /**
     * Получить подписчиков на предмет
     */
    @Transactional(readOnly = true)
    public List<UserSubscriptionEntity> getSubjectSubscribers(Long subjectId) {
        return subscriptionRepository.findSubjectSubscribers(subjectId);
    }

    /**
     * Получить подписчиков на тему
     */
    @Transactional(readOnly = true)
    public List<UserSubscriptionEntity> getTopicSubscribers(Long topicId) {
        return subscriptionRepository.findTopicSubscribers(topicId);
    }

    /**
     * Проверить наличие подписки на новости
     */
    public boolean isSubscribedToNews(Long accountId) {
        return subscriptionRepository
                .findByAccountIdAndTypeAndIsActiveTrue(accountId, SubscriptionType.NEWS)
                .isPresent();
    }

    /**
     * Проверить наличие подписки на курс
     */
    public boolean isSubscribedToCourse(Long accountId, Long courseNumber) {
        return subscriptionRepository
                .existsByAccountIdAndTypeAndDirectionIdAndIsActiveTrue(
                        accountId, SubscriptionType.DIRECTION, courseNumber);
    }
}