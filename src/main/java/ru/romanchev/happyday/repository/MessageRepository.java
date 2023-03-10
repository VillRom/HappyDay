package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.romanchev.happyday.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select (count(m) > 0) from Message m where m.user.id = ?1 and upper(m.textIn) like upper(?2)")
    boolean existsByUser_IdAndTextInLikeIgnoreCase(Long userId, String text);
}
