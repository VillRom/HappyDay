package ru.romanchev.happyday.service;

import ru.romanchev.happyday.dto.JokeDto;

import java.util.List;

public interface JokeService {

    String addJoke(JokeDto dto);

    void addJokes(List<JokeDto> jokeDtos);

    String addJokesFromWebsite();

    JokeDto getJoke(Long jokeId);

    JokeDto getRandomJoke();
}
