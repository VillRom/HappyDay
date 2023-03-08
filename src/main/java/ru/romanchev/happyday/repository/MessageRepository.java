package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanchev.happyday.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
