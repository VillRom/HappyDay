package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.romanchev.happyday.model.Joke;

import java.util.List;

public interface JokeRepository extends JpaRepository<Joke, Long> {
    @Query("select j.id from Joke j")
    List<Long> findAllIds();
}
