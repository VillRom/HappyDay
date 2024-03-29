package ru.romanchev.happyday.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.romanchev.happyday.dto.JokeDto;
import ru.romanchev.happyday.mapper.JokeMapper;
import ru.romanchev.happyday.model.Joke;
import ru.romanchev.happyday.repository.JokeRepository;
import ru.romanchev.happyday.service.JokeService;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JokeServiceImpl implements JokeService {

    private final JokeRepository jokeRepository;

    @Override
    @Transactional
    public String addJoke(JokeDto dto) {
        if (dto.getTextJoke().isEmpty()) {
            return "Текст анекдота не может быть пустым";
        }
        jokeRepository.save(JokeMapper.dtoToJoke(dto));
        return "Анекдот сохранен";
    }

    @Override
    @Transactional
    public void addJokes(List<JokeDto> jokeDtos) {
        for (JokeDto dto : jokeDtos) {
            if (dto.getTextJoke().isEmpty()) {
                throw new RuntimeException();
            }
        }
        jokeRepository.saveAll(JokeMapper.dtosToJokes(jokeDtos));
    }

    @Override
    public JokeDto getJoke(Long jokeId) {
        return JokeMapper.jokeToDto(jokeRepository.findById(jokeId).orElseThrow());
    }

    @Override
    public JokeDto getRandomJoke() {
        long  u = jokeRepository.count();
        Long ran = randomNumberId();
        //if (jokeRepository.count() == 0) return new JokeDto();
        return JokeMapper.jokeToDto(jokeRepository.findById(randomNumberId()).orElse(new Joke()));
        //return JokeMapper.jokeToDto(jokeRepository.findRandomJoke());
    }

    private Long randomNumberId() {
        List<Long> idJokes = jokeRepository.findAllIds();
        if (idJokes.isEmpty()) return 0L;
        return idJokes.get(new Random().nextInt(idJokes.size()));
       // return (long) new Random().nextInt((int) jokeRepository.count()) + 1;
    }
}
