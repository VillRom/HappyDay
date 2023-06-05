package ru.romanchev.happyday.mapper;

import lombok.experimental.UtilityClass;
import ru.romanchev.happyday.dto.JokeDto;
import ru.romanchev.happyday.model.Joke;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class JokeMapper {

    public static Joke dtoToJoke (JokeDto dto) {
        Joke joke = new Joke();
        joke.setTextJoke(dto.getTextJoke());
        joke.setId(dto.getId());
        return joke;
    }

    public static JokeDto jokeToDto (Joke joke) {
        JokeDto dto = new JokeDto();
        dto.setId(joke.getId());
        dto.setTextJoke(joke.getTextJoke());
        return dto;
    }

    public static List<Joke> dtosToJokes(List<JokeDto> dtos) {
        return dtos.stream().map(JokeMapper::dtoToJoke).collect(Collectors.toList());
    }
}
