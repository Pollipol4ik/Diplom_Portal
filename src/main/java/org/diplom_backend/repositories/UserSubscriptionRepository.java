package org.diplom_backend.repositories;

import org.diplom_backend.model.UserSubscriptionEntity;
import org.diplom_backend.model.UserSubscriptionEntity.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscriptionEntity, Long> {

    List<UserSubscriptionEntity> findByAccountIdAndIsActiveTrue(Long accountId);

    Optional<UserSubscriptionEntity> findByAccountIdAndTypeAndIsActiveTrue(
            Long accountId, SubscriptionType type);

    Optional<UserSubscriptionEntity> findByAccountIdAndTypeAndDirectionIdAndIsActiveTrue(
            Long accountId, SubscriptionType type, Long directionId);

    Optional<UserSubscriptionEntity> findByAccountIdAndTypeAndSubjectIdAndIsActiveTrue(
            Long accountId, SubscriptionType type, Long subjectId);

    Optional<UserSubscriptionEntity> findByAccountIdAndTypeAndTopicIdAndIsActiveTrue(
            Long accountId, SubscriptionType type, Long topicId);

    @Query("SELECT s FROM UserSubscriptionEntity s WHERE s.type = 'NEWS' AND s.isActive = true")
    List<UserSubscriptionEntity> findAllNewsSubscribers();

    @Query("SELECT s FROM UserSubscriptionEntity s WHERE s.type = 'DIRECTION' " +
            "AND s.directionId = :directionId AND s.isActive = true")
    List<UserSubscriptionEntity> findCourseSubscribers(@Param("directionId") Long directionId);


    boolean existsByAccountIdAndTypeAndDirectionIdAndIsActiveTrue(
            Long accountId, SubscriptionType type, Long directionId);

    boolean existsByAccountIdAndTypeAndSubjectIdAndIsActiveTrue(
            Long accountId, SubscriptionType type, Long subjectId);

    boolean existsByAccountIdAndTypeAndTopicIdAndIsActiveTrue(
            Long accountId, SubscriptionType type, Long topicId);

    Optional<UserSubscriptionEntity> findByAccountIdAndTypeAndDirectionIdAndSubjectIdAndIsActiveTrue(
            Long accountId,
            UserSubscriptionEntity.SubscriptionType type,
            Long directionId,
            Long subjectId
    );


    @Query("SELECT s FROM UserSubscriptionEntity s " +
            "WHERE s.type = 'TOPIC' " +
            "AND s.topicId = :topicId " +
            "AND s.isActive = true")
    List<UserSubscriptionEntity> findTopicSubscribers(@Param("topicId") Long topicId);


    @Query("SELECT s FROM UserSubscriptionEntity s " +
            "WHERE s.type = 'SUBJECT' " +
            "AND s.subjectId = :subjectId " +
            "AND s.isActive = true")
    List<UserSubscriptionEntity> findSubjectSubscribers(@Param("subjectId") Long subjectId);

    Optional<UserSubscriptionEntity> findByAccountIdAndTypeAndDirectionIdAndSubjectIdAndTopicId(
            Long accountId,
            SubscriptionType type,
            Long directionID,
            Long subjectId,
            Long topicId);

}
