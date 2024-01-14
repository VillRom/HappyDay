package ru.romanchev.happyday.service.Impl;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.romanchev.happyday.dto.JokeDto;
import ru.romanchev.happyday.mapper.JokeMapper;
import ru.romanchev.happyday.model.Joke;
import ru.romanchev.happyday.repository.JokeRepository;
import ru.romanchev.happyday.service.JokeService;

import java.io.IOException;
import java.util.ArrayList;
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
    @Transactional
    public String addJokesFromWebsite() {
        List<JokeDto> jokeDtos = parsingWebsite();
        jokeRepository.saveAll(JokeMapper.dtosToJokes(jokeDtos));
        return "Анекдоты добавлены в БД";
    }

    private List<JokeDto> parsingWebsite() {
        List<JokeDto> jokes = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.anekdot.ru/best/anekdot/0111/")
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("https://www.google.com")
                    .get();
            Elements elements = doc.getElementsByClass("topicbox");
            for (Element el : elements){
                if (el.childNodeSize() >= 2 && el.attributesSize() > 1) {
                    JokeDto dto = new JokeDto();
                    dto.setTextJoke(stSb(el.child(1).text()));
                    jokes.add(dto);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jokes;
    }

    @Override
    public JokeDto getJoke(Long jokeId) {
        return JokeMapper.jokeToDto(jokeRepository.findById(jokeId).orElseThrow());
    }

    @Override
    public JokeDto getRandomJoke() {
        return JokeMapper.jokeToDto(jokeRepository.findById(randomNumberId()).orElse(new Joke()));
    }

    private Long randomNumberId() {
        List<Long> idJokes = jokeRepository.findAllIds();
        if (idJokes.isEmpty()) return 0L;
        return idJokes.get(new Random().nextInt(idJokes.size()));
    }

    private String stSb(String s) {
        StringBuilder sb = new StringBuilder();
        int index;
        for (int i = 0; i < s.length(); i++) {
            if (!s.contains("\\p{Punct} - ") && !s.contains("\\p{Punct} — ")) {
                sb.append(s);
                break;
            } else if (s.contains(" - ")) {
                sb.append(s, 0, s.indexOf(" - ")).append("\n");
                index = s.indexOf(" - ") + 1;
                s = s.substring(index);
            } else {
                sb.append(s, 0, s.indexOf(" — ")).append("\n");
                index = s.indexOf(" — ") + 1;
                s = s.substring(index);
            }
        }
        return sb.toString();
    }
}
