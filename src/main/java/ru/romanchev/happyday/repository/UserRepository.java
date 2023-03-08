package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.romanchev.happyday.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select (count(u) > 0) from User u where u.chatId = ?1")
    boolean existsUserByChatId(Long chatId);
}
