package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanchev.happyday.model.Phrase;

public interface PhrasesRepository extends JpaRepository<Phrase, Long> {
}
