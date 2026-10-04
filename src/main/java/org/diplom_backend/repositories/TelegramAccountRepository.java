package org.diplom_backend.repositories;


import org.diplom_backend.model.TelegramAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramAccountRepository extends JpaRepository<TelegramAccountEntity, Long> {

    Optional<TelegramAccountEntity> findByChatId(Long chatId);

    List<TelegramAccountEntity> findByAccountId(Long accountId);

    boolean existsByChatId(Long chatId);

    void deleteByChatId(Long chatId);
}
