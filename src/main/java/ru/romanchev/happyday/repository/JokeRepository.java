package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanchev.happyday.model.Joke;

public interface JokeRepository extends JpaRepository<Joke, Long> {
}
