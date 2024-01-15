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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String addJokesFromWebsite() {
        List<JokeDto> jokeDtos = parsingWebsite();
        addJokes(jokeDtos);
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
                    dto.setTextJoke(modificationOfTheStringForAnAnecdote(el.child(1).text()));
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

    private String modificationOfTheStringForAnAnecdote(String s) {
        Pattern p =  Pattern.compile("\\p{Punct}");
        StringBuilder sb = new StringBuilder();
        int index;
        for (int i = 0; i < s.length(); i++) {
            if (!s.contains(" - ") && !s.contains(" — ")) {
                sb.append(s);
                break;
            } else if (s.contains(" - ")) {
                Matcher m = p.matcher(s.substring(s.indexOf(" - ") - 1, s.indexOf(" - ")));
                sb.append(s, 0, s.indexOf(" - ")).append(" ");
                index = s.indexOf(" - ") + 1;
                s = s.substring(index);
                if (m.find()) sb.append("\n");
            } else {
                Matcher m = p.matcher(s.substring(0, s.indexOf(" — ")));
                sb.append(s, 0, s.indexOf(" — ")).append(" ");
                index = s.indexOf(" — ") + 1;
                s = s.substring(index);
                if (m.find()) sb.append("\n");
            }
        }
        return sb.toString();
    }
}
