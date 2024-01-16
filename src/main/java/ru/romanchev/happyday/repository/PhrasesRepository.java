package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.romanchev.happyday.model.Phrase;

public interface PhrasesRepository extends JpaRepository<Phrase, Long> {
    @Query("SELECT max(id) FROM Phrase")
    Long getMaxId();
}
